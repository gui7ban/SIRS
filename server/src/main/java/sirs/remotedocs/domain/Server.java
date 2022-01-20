package sirs.remotedocs.domain;

import sirs.remotedocs.Logger;
import sirs.remotedocs.ServerRepo;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Server {

	private ServerRepo serverRepo = new ServerRepo();
	private Logger logger = new Logger("Server", "Core");

	public void login(String name, String password) throws RemoteDocsException {
		User user = this.serverRepo.getUser(name);
		if (user == null)
			throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);

		try {
			byte[] salt = Base64.getDecoder().decode(user.getSalt());
			if (!HashOperations.verifyDigest(password, user.getHashedPassword(), salt))
				throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);
		} catch (NoSuchAlgorithmException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
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

	public synchronized String ping() {
		return "I'm alive!";
}
	
}
