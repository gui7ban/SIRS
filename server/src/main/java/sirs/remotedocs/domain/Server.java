package sirs.remotedocs.domain;

import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;
public class Server {

	public void login (String name, String password) throws RemoteDocsException {
	
	} 
	
	public void register(String name, String password) throws RemoteDocsException {
	
	}

	public synchronized String ping() {
		return "I'm alive!";
}
	
}
