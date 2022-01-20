/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sirs.remotedocs;

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
        System.out.println(ClientApp.class.getSimpleName());
        System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		// check arguments
		if (args.length != 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", ClientApp.class.getName());
            System.exit(-1);
		}

        final String host = args[0];
		final int port = Integer.parseInt(args[1]);
       
        new ClientApp(host, port);

    }
}
