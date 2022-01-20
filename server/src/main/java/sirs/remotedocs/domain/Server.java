package sirs.remotedocs.domain;

import sirs.remotedocs.Logger;
import sirs.remotedocs.ServerRepo;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class Server {

	private ServerRepo serverRepo = new ServerRepo();
	private Logger logger = new Logger("Server", "Core");
	private Map<String, String> accessTokens = new TreeMap<>();

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

	public int createFile(String name, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

		try {
			int nextId = this.serverRepo.getMaxFileId() + 1;
			File newFile = new File(String.valueOf(nextId));

			boolean fileExists = this.serverRepo.fileExists(username, name);
			if(fileExists || !newFile.createNewFile())
				throw new RemoteDocsException(ErrorMessage.FILE_ALREADY_EXISTS);

			this.serverRepo.createFile(nextId, name, username);
			return nextId;
		} catch (IOException | SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void uploadFile(int id, byte[] content, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

		File file = new File(String.valueOf(id));
		if (!file.exists())
			throw new RemoteDocsException(ErrorMessage.FILE_DOESNT_EXIST);

		try {
			FileDetails fileDetails = this.serverRepo.getFileDetails(id, username);
			if (fileDetails == null)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (fileDetails.getPermission() == 1)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_WRITE);

			FileOutputStream out = new FileOutputStream(file);
			out.write(content);
			out.close();

			String fileDigest = HashOperations.digest(Base64.getEncoder().encodeToString(content), null);
			this.serverRepo.updateFileDigest(id, fileDigest);
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

			File file = new File(String.valueOf(id));
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

		try {
			FileDetails fileDetails = this.serverRepo.getFileDetails(id, username);
			if (fileDetails == null)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_ACCESS);
			else if (fileDetails.getPermission() != 0)
				throw new RemoteDocsException(ErrorMessage.UNAUTHORIZED_FILENAME_CHANGE);

			this.serverRepo.updateFileName(id, newName);
		} catch (SQLException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	// TODO: Share file permissions.

	public synchronized String ping() {
		return "I'm alive!";
}
	
}
