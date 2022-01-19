/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sirs.remotedocs;

import javax.swing.JFrame;

/**
 *
 * @author tomaz
 */
public class ClientApp {
    private Menu menu;
    private LoginRegisterForm login;
    private LoginRegisterForm register;
    private DocumentsList doclist;
    private EditDocumentForm editdoc;
 
    
    public ClientApp(){
        menu = new Menu(this);
        login = new LoginRegisterForm(this, "LOGIN");
        register = new LoginRegisterForm(this, "REGISTER");
        doclist = new DocumentsList(this);
        editdoc = new EditDocumentForm(this);
        menu.setVisible(true);
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
  
    
    public void switchForm(JFrame source, JFrame destination){
        source.setVisible(false);
        destination.setVisible(true);
    }
    
    public static void main(String[] args){
        ClientApp clientApp = new ClientApp();
    }
}
