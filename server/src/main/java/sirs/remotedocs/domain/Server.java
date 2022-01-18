package sirs.remotedocs.domain;

import sirs.remotedocs.Logger;
import sirs.remotedocs.ServerRepo;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.crypto.RandomOperations;
import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;

import java.security.NoSuchAlgorithmException;

public class Server {

	private static final int SALT_LENGTH = 30;
	private ServerRepo serverRepo = new ServerRepo();
	private Logger logger = new Logger("Server", "Core");

	public boolean login(String name, String password) throws RemoteDocsException {
		User user = this.serverRepo.getUser(name);
		if (user == null)
			throw new RemoteDocsException(ErrorMessage.USER_DOESNT_EXIST);

		try {
			String hashedPassword = HashOperations.digest(password + user.getSalt());
			if (!HashOperations.verifyDigest(hashedPassword, user.getHashedPassword()))
				throw new RemoteDocsException(ErrorMessage.INVALID_CREDENTIALS);

			return true;
		} catch (NoSuchAlgorithmException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}
	} 
	
	public void register(String name, String password) throws RemoteDocsException {
		String newSalt = RandomOperations.randomString(SALT_LENGTH);
		User user = this.serverRepo.getUser(name);
		if (user != null)
			throw new RemoteDocsException(ErrorMessage.USER_ALREADY_EXISTS);

		try {
			String hashedPassword = HashOperations.digest(password + newSalt);
			this.serverRepo.registerUser(name, hashedPassword, newSalt);
		} catch (NoSuchAlgorithmException e) {
			this.logger.log(e.getMessage());
			throw new RemoteDocsException(ErrorMessage.INTERNAL_ERROR);
		}

	}

	public synchronized String ping() {
		return "I'm alive!";
}
	
}
