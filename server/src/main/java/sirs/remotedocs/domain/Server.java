package sirs.remotedocs.domain;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import sirs.remotedocs.Logger;
import sirs.remotedocs.ServerFrontend;
import sirs.remotedocs.ServerRepo;
import sirs.remotedocs.backupgrpc.Backupcontract.*;
import sirs.remotedocs.crypto.AsymmetricCryptoOperations;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.crypto.SymmetricCryptoOperations;
import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.*;

public class Server {

	private static final String FILES_DIR = "files/";
	private final ServerRepo serverRepo = new ServerRepo();
	private final Logger logger = new Logger("Server", "Core");
	private final Map<String, String> accessTokens = new TreeMap<>();
	private KeyPair keyPair;

	// Backup Server classes and data
	private static final int MAX_NONCE = 25000;
	private final ServerFrontend serverFrontend;
	private PublicKey backupServerPublicKey;
	private SecretKey backupServerSecretKey;
	private IvParameterSpec backupServerIV;
	private int currentNonce;

	public Server(String backupServerPath) {
		this.serverFrontend = new ServerFrontend(backupServerPath);
		File f = new File(FILES_DIR);
		if(!f.mkdir())
			this.logger.log("Error creating files directory.");

		try {
			this.keyPair = AsymmetricCryptoOperations.generateKeyPair();
			this.exchangePublicKeys();
			this.handshake();

			final int baseDuration = 60 * 1000;
			final Server thisServer = this;
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					thisServer.backupFiles();
					thisServer.logger.log("Performing files backup now...");
				}
			}, baseDuration * 2, baseDuration);
		} catch (NoSuchAlgorithmException e) {
			this.logger.log("Failed to generate key pair for backup server.");
		}
	}

	// Backup Server Methods
	public void exchangePublicKeys() {
		PublicKeyRequest request = PublicKeyRequest
				.newBuilder()
				.setPublicKey(ByteString.copyFrom(this.keyPair.getPublic().getEncoded()))
				.build();

		PublicKeyResponse response = this.serverFrontend.getBackupServerPublicKey(request);
		try {
			this.backupServerPublicKey = AsymmetricCryptoOperations.getPublicKeyFromBytes(
					response.getKey().toByteArray()
			);
			this.logger.log("Successfully exchanged keys with Backup Server!");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			this.logger.log(e.getMessage());
		}
	}

	public void handshake() {
		try {
			this.backupServerSecretKey = SymmetricCryptoOperations.generateKey();
			this.backupServerIV = SymmetricCryptoOperations.generateIV();
			this.currentNonce = new SecureRandom().nextInt(MAX_NONCE);

			// Build unencrypted request.
			HandshakeRequest request = HandshakeRequest
					.newBuilder()
					.setNonce(this.currentNonce)
					.setSecretKey(ByteString.copyFrom(this.backupServerSecretKey.getEncoded()))
					.setInitializationVector(ByteString.copyFrom(this.backupServerIV.getIV()))
					.build();

			byte[] handshakeRequestBytes = request.toByteArray();

			// Encrypt request.
			byte[] encryptedHandshakeRequest = AsymmetricCryptoOperations.encrypt(
					handshakeRequestBytes,
					this.backupServerPublicKey
			);

			// Sign unencrypted request.
			byte[] signedRequest = AsymmetricCryptoOperations.sign(handshakeRequestBytes, this.keyPair.getPrivate());

			EncryptedRequest encryptedRequest = EncryptedRequest
					.newBuilder()
					.setRequest(ByteString.copyFrom(encryptedHandshakeRequest))
					.setSignature(ByteString.copyFrom(signedRequest))
					.build();

			EncryptedResponse encryptedResponse = this.serverFrontend.handshake(encryptedRequest);
			HandshakeResponse handshakeResponse = HandshakeResponse.parseFrom(
					SymmetricCryptoOperations.decrypt(
							encryptedResponse.getResponse().toByteArray(),
							this.backupServerSecretKey,
							this.backupServerIV
					)
			);

			boolean validSignature = AsymmetricCryptoOperations.verifySignature(
					handshakeResponse.toByteArray(),
					this.backupServerPublicKey,
					encryptedResponse.getSignature().toByteArray()
			);

			if (!validSignature) {
				this.logger.log("Could not handshake with backup server: signature doesn't match.");
				return;
			} else if (handshakeResponse.getNonce() != this.currentNonce + 1) {
				this.logger.log("Invalid nonce received from backup server: " + handshakeResponse.getNonce());
				return;
			}

			this.currentNonce = handshakeResponse.getNonce();
			this.logger.log("Successful handshake with Backup Server!");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| InvalidKeyException | SignatureException
				| InvalidAlgorithmParameterException | InvalidProtocolBufferException e) {
			this.logger.log(e.getMessage());
		}
	}

	public void backupFiles() {
		try {
			Map<Integer, String> fileDigests = this.serverRepo.getFilesDigests();

			File directory = new File(FILES_DIR);
			File[] files = directory.listFiles();
			if (files == null) {
				this.logger.log("No files found to backup.");
				return;
			}

			FilesRequest.Builder requestBuilder = FilesRequest.newBuilder();

			for (File file: files) {
				int fileId = Integer.parseInt(file.getName());
				FileInputStream in = new FileInputStream(file);
				byte[] fileContent = in.readAllBytes();
				in.close();

				requestBuilder.addFiles(
						FileInfo
								.newBuilder()
								.setContent(ByteString.copyFrom(fileContent))
								.setId(fileId)
								.setDigest(fileDigests.get(fileId))
								.build()
				);
			}

			this.currentNonce += 1;
			FilesRequest filesRequest = requestBuilder.setNonce(this.currentNonce).build();

			byte[] encryptedFilesRequest = SymmetricCryptoOperations.encrypt(
					filesRequest.toByteArray(),
					this.backupServerIV.getIV(),
					this.backupServerSecretKey
			);

			byte[] signedRequest = AsymmetricCryptoOperations.sign(
					filesRequest.toByteArray(),
					this.keyPair.getPrivate()
			);

			EncryptedRequest encryptedRequest = EncryptedRequest
					.newBuilder()
					.setRequest(ByteString.copyFrom(encryptedFilesRequest))
					.setSignature(ByteString.copyFrom(signedRequest))
					.build();

			EncryptedResponse encryptedResponse = this.serverFrontend.backupServerFiles(encryptedRequest);
			FilesResponse filesResponse = FilesResponse.parseFrom(
					SymmetricCryptoOperations.decrypt(
							encryptedResponse.getResponse().toByteArray(),
							this.backupServerSecretKey,
							this.backupServerIV
					)
			);

			boolean validSignature = AsymmetricCryptoOperations.verifySignature(
					filesResponse.toByteArray(),
					this.backupServerPublicKey,
					encryptedResponse.getSignature().toByteArray()
			);

			if (!validSignature) {
				this.logger.log("Signature does not match for FilesResponse from Backup Server.");
				return;
			}

			// Check if any files were not backed up. Send an alert in such a case.
			List<Integer> fileIds = filesResponse.getIdsList();
			if (fileIds.size() > 0)
				this.logger.log("[WARNING] The following files were not backed up since the digest associated to the" +
						" last change does not match the file's digest: " + Arrays.toString(fileIds.toArray()));
			else
				this.logger.log("All files were backed up successfully.");
		} catch (SQLException | IOException | InvalidAlgorithmParameterException | NoSuchPaddingException
				| IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException
				| InvalidKeyException | SignatureException e) {
			this.logger.log(e.getMessage());
		}
	}

	// Server Methods
	public String login(String name, String password) throws RemoteDocsException {
		try {
			User user = this.serverRepo.getUser(name);
			if (user == null)
				throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);
			byte[] salt = Base64.getDecoder().decode(user.getSalt());
			if (!HashOperations.verifyDigest(password, user.getHashedPassword(), salt))
				throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);

			// Generate access token for session:
			String accessToken = Base64.getEncoder().encodeToString(HashOperations.generateSalt());

			// Add the token to the access tokens map:
			this.accessTokens.put(name, accessToken);

			return accessToken;
		} catch (NoSuchAlgorithmException | SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public List<FileDetails> getListDocuments(String username, String token) throws RemoteDocsException {
		if (token != null && !this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		try {
			return this.serverRepo.getListDocuments(username);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	private boolean isSessionValid(String username, String token) {
		return this.accessTokens.containsKey(username) && this.accessTokens.get(username).equals(token);
	}

	public void logout(String username, String token) throws RemoteDocsException {
		try {
			User user = this.serverRepo.getUser(username);
			if (user == null)
				throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);
			else if (!this.isSessionValid(username, token))
				throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

			this.accessTokens.remove(username);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}
	
	public String register(String name, String password) throws RemoteDocsException {
		try {
			if(name.isBlank())
				throw new RemoteDocsException(ErrorMessage.INVALID_USERNAME);
			User user = this.serverRepo.getUser(name);
			if (user != null)
				throw new RemoteDocsException(ErrorMessage.USER_ALREADY_EXISTS);
			else if(password != null && password.length() < 7)
				throw new RemoteDocsException(ErrorMessage.INVALID_PASSWORD);
			else {
				byte[] newSalt = HashOperations.generateSalt();
				String saltInString = Base64.getEncoder().encodeToString(newSalt);
				newSalt = Base64.getDecoder().decode(saltInString);
				String hashedPassword = HashOperations.digest(password,newSalt);
				this.serverRepo.registerUser(name, hashedPassword, saltInString);

				// Generate access token for session:
				String accessToken = Base64.getEncoder().encodeToString(HashOperations.generateSalt());

				// Add the token to the access tokens map:
				this.accessTokens.put(name, accessToken);
				return accessToken;
			}
		} catch (NoSuchAlgorithmException | SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public FileDetails createFile(String name, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		if (name.isBlank())
			throw new RemoteDocsException(ErrorMessage.FILE_NAME_EMPTY);
		try {
			
			int nextId = this.serverRepo.getMaxFileId() + 1; 
			File newFile = new File(FILES_DIR + nextId);

			boolean fileExists = this.serverRepo.fileExists(username, name);
			if(fileExists || !newFile.createNewFile())
				throw new RemoteDocsException(ErrorMessage.FILE_ALREADY_EXISTS);
			return this.serverRepo.createFile(nextId, name, username);
			 
		} catch (IOException | SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void uploadFile(int id, byte[] content, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

		File file = new File(FILES_DIR + id);
		if (!file.exists())
			throw new RemoteDocsException(ErrorMessage.FILE_DOESNT_EXIST);

		try {
			int permission = this.serverRepo.getFilePermission(id, username);
			if (permission == -1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (permission == 1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_WRITE);
			
			
			String fileDigest = HashOperations.digest(content);
			this.serverRepo.updateFileDigest(id, fileDigest, username);
			
			
			FileOutputStream out = new FileOutputStream(file);
			out.write(content);
			out.close();

			
		} catch (IOException | NoSuchAlgorithmException | SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public FileDetails downloadFile(int id, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

		try {
			FileDetails fileDetails = this.serverRepo.getFileDetails(id, username);
			if (fileDetails == null)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			// TODO: check digest
			File file = new File(FILES_DIR + id);
			if (!file.exists())
				throw new RemoteDocsException(ErrorMessage.FILE_DOESNT_EXIST);

			FileInputStream in = new FileInputStream(file);
			fileDetails.setContent(in.readAllBytes());
			in.close();

			return fileDetails;
		} catch (SQLException | IOException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void updateFileName(int id, String newName, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		if (newName.isBlank())
			throw new RemoteDocsException(ErrorMessage.FILE_NAME_EMPTY);
		try {
			int permission = this.serverRepo.getFilePermission(id, username);
			if (permission == -1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (permission != 0)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_FILENAME_CHANGE);

			this.serverRepo.updateFileName(id, newName);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void deleteFile(int id, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		try {
			int permission = this.serverRepo.getFilePermission(id, username);
			if (permission == -1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (permission != 0)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_FILE_DELETION);
			File file = new File(FILES_DIR + String.valueOf(id));
			file.delete();
			this.serverRepo.deleteFile(id);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public List<User> getNotOwnerUsersOfDoc(int id, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		try {
			int permission = this.serverRepo.getFilePermission(id, username);
			if (permission == -1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (permission != 0)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_FILE_LIST_ACCESS);
			
			return this.serverRepo.getUsersExceptOwnerOfDoc(id);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void updatePermission(String owner, String token, String username, int id, int permission) throws RemoteDocsException {
		if (!this.isSessionValid(owner, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		try {
			int permissionOfOwner = this.serverRepo.getFilePermission(id, owner);
			if (permissionOfOwner == -1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (permissionOfOwner != 0)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_FILE_PERMISSIONS);
			this.serverRepo.updatePermission(username, id, permission);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void addPermission(String owner, String token, String username, int id, int permission) throws RemoteDocsException {
		if (!this.isSessionValid(owner, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		try {
			int permissionOfOwner = this.serverRepo.getFilePermission(id, owner);
			if (permissionOfOwner == -1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (permissionOfOwner != 0)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_FILE_PERMISSIONS);
			User user = this.serverRepo.getUser(username);
			if (user == null)
				throw new RemoteDocsException(ErrorMessage.USER_DOESNT_EXIST);
			this.serverRepo.addPermission(username, id, permission,true);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			if(e.getMessage().contains("ERROR: duplicate key value violates unique constraint"))
				throw new RemoteDocsException(ErrorMessage.USER_ALREADY_HAVE_PERMISSION);
			else
				throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void deletePermission(String owner, String token, String username, int id) throws RemoteDocsException {
		if (!this.isSessionValid(owner, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);
		try {
			int permissionOfOwner = this.serverRepo.getFilePermission(id, owner);
			if (permissionOfOwner == -1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (permissionOfOwner != 0)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_FILE_PERMISSIONS);
			User user = this.serverRepo.getUser(username);
			if (user == null)
				throw new RemoteDocsException(ErrorMessage.USER_DOESNT_EXIST);
			this.serverRepo.deletePermission(username, id);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}


	public synchronized String ping() {
		return "I'm alive!";
}
	
}
