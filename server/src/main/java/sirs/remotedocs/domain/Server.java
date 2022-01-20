package sirs.remotedocs.domain;

import sirs.remotedocs.Logger;
import sirs.remotedocs.ServerRepo;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;


public class Server {

	private ServerRepo serverRepo = new ServerRepo();
	private Logger logger = new Logger("Server", "Core");
	private Map<String, String> accessTokens = new TreeMap<>();

	public void login(String name, String password) throws RemoteDocsException {
		User user = this.serverRepo.getUser(name);
		if (user == null)
			throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);

		try {
			byte[] salt = Base64.getDecoder().decode(user.getSalt());
			if (!HashOperations.verifyDigest(password, user.getHashedPassword(), salt))
				throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);

			// Generate access token for session:
			String accessToken = Base64.getEncoder().encodeToString(HashOperations.generateSalt());

			// Add the token to the access tokens map:
			this.accessTokens.put(name, accessToken);
		} catch (NoSuchAlgorithmException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	private boolean isSessionValid(String username, String token) {
		return this.accessTokens.containsKey(username) && this.accessTokens.get(username).equals(token);
	}

	public void logout(String username, String token) throws RemoteDocsException {
		User user = this.serverRepo.getUser(username);
		if (user == null)
			throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);
		else if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

		this.accessTokens.remove(username);
	}
	
	public void register(String name, String password) throws RemoteDocsException {

		User user = this.serverRepo.getUser(name);
		if (user != null)
			throw new RemoteDocsException(ErrorMessage.USER_ALREADY_EXISTS);
		else if(password != null && password.length() < 7)
			throw new RemoteDocsException(ErrorMessage.INVALID_PASSWORD);
		else {
			try {
				byte[] newSalt = HashOperations.generateSalt();
				String saltInString = Base64.getEncoder().encodeToString(newSalt);
				newSalt = Base64.getDecoder().decode(saltInString);
				String hashedPassword = HashOperations.digest(password,newSalt);
				this.serverRepo.registerUser(name, hashedPassword, saltInString);
			} catch (NoSuchAlgorithmException e) {
				this.logger.log(e.getMessage());
				throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
			}
		}
	}

	public String createFile(String name, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

		String fileId = username + "/" + name;
		File newFile = new File(fileId);
		try {
			if(!newFile.createNewFile())
				throw new RemoteDocsException(ErrorMessage.FILE_ALREADY_EXISTS);

			this.serverRepo.createFile(fileId, name, username);
			return fileId;
		} catch (IOException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public void uploadFile(String name, byte[] content, String username, String token) throws RemoteDocsException {
		if (!this.isSessionValid(username, token))
			throw new RemoteDocsException(ErrorMessage.INVALID_SESSION);

		String fileId = username + "/" + name;
		File file = new File(fileId);
		if (!file.exists())
			throw new RemoteDocsException(ErrorMessage.FILE_DOESNT_EXIST);

		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(content);

			String fileDigest = HashOperations.digest(Base64.getEncoder().encodeToString(content), null);
		} catch (IOException | NoSuchAlgorithmException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	}

	public synchronized String ping() {
		return "I'm alive!";
}
	
}
