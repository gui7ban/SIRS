package sirs.remotedocs.domain;

import sirs.remotedocs.Logger;
import sirs.remotedocs.ServerRepo;
import sirs.remotedocs.domain.exception.RemoteDocsException;
public class Server {

	private ServerRepo serverRepo = new ServerRepo();
	private Logger logger = new Logger("Server", "Core");

	public void login (String name, String password) throws RemoteDocsException {
	
	} 
	
	public void register(String name, String password) throws RemoteDocsException {
	
	}

	public synchronized String ping() {
		return "I'm alive!";
}
	
}
