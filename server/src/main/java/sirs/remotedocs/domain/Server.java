package sirs.remotedocs.domain;

import com.google.protobuf.ByteString;
import sirs.remotedocs.Logger;
import sirs.remotedocs.ServerFrontend;
import sirs.remotedocs.ServerRepo;
import sirs.remotedocs.backupgrpc.Backupcontract.*;
import sirs.remotedocs.crypto.AsymmetricCryptoOperations;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;

import java.io.*;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.*;

public class Server {

	private static final String FILES_DIR = "files/";
	private ServerRepo serverRepo = new ServerRepo();
	private Logger logger = new Logger("Server", "Core");
	private Map<String, String> accessTokens = new TreeMap<>();
	private KeyPair keyPair;

	// Backup Server classes and data
	private final ServerFrontend serverFrontend;
	private PublicKey backupServerPublicKey;
	private final Map<Integer, Boolean> nonces = new HashMap<>();

	public Server(String backupServerPath) {
		this.serverFrontend = new ServerFrontend(backupServerPath);

		try {
			File f = new File(FILES_DIR);
			if(!f.mkdir())
				this.logger.log("Error creating files directory.");
			this.keyPair = AsymmetricCryptoOperations.generateKeyPair();
			this.exchangePublicKeys();
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
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
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
			
			
			String fileDigest = HashOperations.digest(Base64.getEncoder().encodeToString(content), null);
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
			this.serverRepo.addPermission(username, id, permission);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public synchronized String ping() {
		return "I'm alive!";
}
	
}
