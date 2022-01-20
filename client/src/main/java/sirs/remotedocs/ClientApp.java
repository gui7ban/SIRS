/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sirs.remotedocs;

import io.grpc.StatusRuntimeException;
import sirs.remotedocs.grpc.Contract.*;

import javax.swing.*;

public class ClientApp {
    private ServerFrontend frontend;
    private Menu menu;
    private LoginRegisterForm login;
    private LoginRegisterForm register;
    private DocumentsList doclist;
    private EditDocumentForm editdoc;
 
    
    public ClientApp(String host, int port){
        frontend = new ServerFrontend(host, port);
        menu = new Menu(this);
        login = new LoginRegisterForm(this, "LOGIN");
        register = new LoginRegisterForm(this, "REGISTER");
        doclist = new DocumentsList(this);
        editdoc = new EditDocumentForm(this);
        menu.setVisible(true);
    }

    /*--------------------GETTERS--------------------*/
    public ServerFrontend getFrontend() {
        return frontend;
    }

    public Menu getMenu() {
        return menu;
    }

    public LoginRegisterForm getLogin() {
        return login;
    }

    public LoginRegisterForm getRegister() {
        return register;
    }

    public DocumentsList getDoclist() {
        return doclist;
    }

    public EditDocumentForm getEditdoc() {
        return editdoc;
    }
    /*-----------------------------------------------*/

    public void switchForm(JFrame source, JFrame destination){
        source.setVisible(false);
        destination.setVisible(true);
    }
    
    public static void main(String[] args){
        System.out.println(ClienteApp.class.getSimpleName());
        System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		// check arguments
		if (args.length != 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", ClienteApp.class.getName());
            System.exit(-1);
		}

        final String host = args[0];
		final int port = Integer.parseInt(args[1]);
        ServerFrontend frontend = new ServerFrontend(host, port);
		PingRequest pingRequest = PingRequest.newBuilder().build();
		PingResponse pingResponse = frontend.ping(pingRequest);
		System.out.println(pingResponse.getOutputText());

		RegisterRequest registerRequest = RegisterRequest.newBuilder().setUsername("Gui").setPassword("123456789").build();
		try {
			RegisterResponse registerResponse = frontend.register(registerRequest);
		}
		catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
			e.getStatus().getDescription());
		}
		LoginRequest loginRequest = LoginRequest.newBuilder().setUsername("Gui").setPassword("123456789").build();
		try {
			LoginResponse loginResponse = frontend.login(loginRequest);
		}
		catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
			e.getStatus().getDescription());
		}
		frontend.channelEnd();
        ClientApp clientApp = new ClientApp(host, port);

    }
}
