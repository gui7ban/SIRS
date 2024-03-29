/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sirs.remotedocs;

import sirs.remotedocs.crypto.AsymmetricCryptoOperations;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.crypto.SymmetricCryptoOperations;
import sirs.remotedocs.grpc.Contract.*;
import io.grpc.StatusRuntimeException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.JOptionPane;

import com.google.protobuf.ByteString;


/**
 *
 * @author tomaz
 */
public class LoginRegisterForm extends javax.swing.JFrame {
    
    private ClientApp clientApp;
    /**
     * Creates new form LoginRegisterForm
     * @param formSelector
     * @param option
     */
    public LoginRegisterForm(ClientApp formSelector, String option) {
        initComponents();
        LoginRegister_btn.setText(option); 
        back_btn.requestFocusInWindow();
        this.clientApp = formSelector;
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        username_tf = new javax.swing.JTextField();
        password_tf = new javax.swing.JPasswordField();
        LoginRegister_btn = new javax.swing.JButton();
        back_btn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 102, 51));

        username_tf.setBackground(new java.awt.Color(255, 102, 51));
        username_tf.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        username_tf.setForeground(new java.awt.Color(255, 255, 255));
        username_tf.setText("Username");
        username_tf.setBorder(null);
        username_tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                username_tfFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                username_tfFocusLost(evt);
            }
        });

        password_tf.setBackground(new java.awt.Color(255, 102, 51));
        password_tf.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        password_tf.setForeground(new java.awt.Color(255, 255, 255));
        password_tf.setText("password");
        password_tf.setBorder(null);
        password_tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                password_tfFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                password_tfFocusLost(evt);
            }
        });
        password_tf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                password_tfActionPerformed(evt);
            }
        });

        LoginRegister_btn.setBackground(new java.awt.Color(255, 255, 204));
        LoginRegister_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LoginRegister_btn.setText("LOGIN");
        LoginRegister_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LoginRegister_btnMouseClicked(evt);
            }
        });

        back_btn.setBackground(new java.awt.Color(255, 255, 204));
        back_btn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        back_btn.setText("BACK");
        back_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                back_btnMouseClicked(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        jSeparator2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8-pessoa-do-sexo-masculino-24.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8-desbloquear-24.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Alien Encounters", 1, 30)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("REMOTE DOCS");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(password_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(18, 18, 18)
                            .addComponent(username_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(back_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LoginRegister_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel3)
                .addGap(65, 65, 65)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(username_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(password_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(back_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LoginRegister_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void back_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back_btnMouseClicked
        clientApp.switchForm(this, clientApp.getMenu());
    }//GEN-LAST:event_back_btnMouseClicked

    private void password_tfFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_password_tfFocusGained
        password_tf.setText("");
    }//GEN-LAST:event_password_tfFocusGained

    private void username_tfFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_username_tfFocusGained
        if (username_tf.getText().equals("Username")) {
            username_tf.setText("");
        }   
    }//GEN-LAST:event_username_tfFocusGained

    private void password_tfFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_password_tfFocusLost

    }//GEN-LAST:event_password_tfFocusLost

    private void username_tfFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_username_tfFocusLost
        if (username_tf.getText().isEmpty()) {
            username_tf.setText("Username");
        }
    }//GEN-LAST:event_username_tfFocusLost

    private void password_tfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_password_tfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_password_tfActionPerformed

    private void LoginRegister_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginRegister_btnMouseClicked
        String password = new String(password_tf.getPassword());
        String username = username_tf.getText();
        if(LoginRegister_btn.getText().equals("LOGIN")){
            LoginRequest loginRequest = LoginRequest.newBuilder().setUsername(username).setPassword(password).build();
		    try {
			    LoginResponse loginResponse = clientApp.getFrontend().login(loginRequest);
                PublicKey publicKey = AsymmetricCryptoOperations.getPublicKeyFromBytes(loginResponse.getPublicKey().toByteArray());
                
                byte[] salt = loginResponse.getSalt().toByteArray();
                SecretKey secretKey = SymmetricCryptoOperations.getSecretKey(password, salt);
                
                byte[] privateKeyBytes = SymmetricCryptoOperations.decrypt(
                    loginResponse.getPrivateKey().toByteArray(),
                    secretKey, new IvParameterSpec(salt));
                PrivateKey privateKey = AsymmetricCryptoOperations.getPrivateKeyFromBytes(privateKeyBytes);
                    
                clientApp.setPublicKey(publicKey);
                clientApp.setPrivateKey(privateKey);

                password_tf.setText("");
                username_tf.setText("Username");
                
                List<DocumentInfo> listGrpc = loginResponse.getDocumentsList();
                Map<Integer,FileDetails> listDocs = new TreeMap<>(); 
                
                for (DocumentInfo docGrpc: listGrpc){
                    listDocs.put(docGrpc.getId(), new FileDetails(docGrpc.getId(), docGrpc.getName(), docGrpc.getRelationship()));
                }
                
                clientApp.setFiles(listDocs);
                clientApp.loginOrRegister(username, loginResponse.getToken());
                DocumentsList docForm = clientApp.getDoclist();
                docForm.setMyDocumentsList(clientApp.getMyDocs());
                docForm.setSharedList(clientApp.getSharedWithMe());
                docForm.clearSelectionOfLists();
                clientApp.switchForm(this, docForm);
                
            }
		    catch (StatusRuntimeException e) {
			    System.out.println("Caught exception with description: " +
			    e.getStatus().getDescription());
                JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
		    }
            catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
                System.out.println("Caught exception with description: " +
			    e.getMessage());
                JOptionPane.showMessageDialog(null, "There was an error on the server which prevented the operation from being executed.");
            }
        }
        else{
            
            try {
                KeyPair keyPair = AsymmetricCryptoOperations.generateKeyPair();
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();
                clientApp.setPrivateKey(privateKey);
                clientApp.setPublicKey(publicKey);
				
                byte[] newSalt = HashOperations.generateSalt();
                String saltInString = Base64.getEncoder().encodeToString(newSalt);
				newSalt = Base64.getDecoder().decode(saltInString);

                SecretKey secretKey = SymmetricCryptoOperations.getSecretKey(password, newSalt);
                byte[] privateKeyBytes = privateKey.getEncoded();
                byte[] privateKeyEncrypted = SymmetricCryptoOperations.encrypt(privateKeyBytes, newSalt, secretKey);
                RegisterRequest registerRequest = RegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setPublicKey(ByteString.copyFrom(publicKey.getEncoded()))
                .setPrivateKey(ByteString.copyFrom(privateKeyEncrypted))
                .setSalt(ByteString.copyFrom(newSalt))
                .build();

			    RegisterResponse registerResponse = clientApp.getFrontend().register(registerRequest);
 
                password_tf.setText("");
                username_tf.setText("Username");
 
                clientApp.loginOrRegister(username, registerResponse.getToken());
                clientApp.switchForm(this, clientApp.getDoclist());
                
            }
		    catch (StatusRuntimeException e) {
			    System.out.println("Caught exception with description: " +
			    e.getStatus().getDescription());
                JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
		    } catch (InvalidKeySpecException | NoSuchAlgorithmException
            | InvalidKeyException | NoSuchPaddingException
            | InvalidAlgorithmParameterException | BadPaddingException
            | IllegalBlockSizeException e) {
                System.out.println("Caught exception with description: " +
			   "internal error");
               e.printStackTrace();
               //TODO: o user pode já ter sido criado no server reverter isso.
                JOptionPane.showMessageDialog(null, "There was an error on the server which prevented the operation from being executed.");
            }
        }     
    }//GEN-LAST:event_LoginRegister_btnMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.dispose();
        clientApp.getFrontend().channelEnd();
        System.exit(0);   
     }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton LoginRegister_btn;
    private javax.swing.JButton back_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPasswordField password_tf;
    private javax.swing.JTextField username_tf;
    // End of variables declaration//GEN-END:variables
}
