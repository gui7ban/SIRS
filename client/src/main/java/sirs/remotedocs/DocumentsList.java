/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sirs.remotedocs;

import java.awt.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;

import io.grpc.StatusRuntimeException;
import sirs.remotedocs.grpc.Contract.*;
/**
 *
 * @author tomaz
 */
public class DocumentsList extends javax.swing.JFrame {
    
    private ClientApp clientApp;
    /**
     * Creates new form Dashboard
     */
    public DocumentsList(ClientApp clientApp) {
        initComponents();
        this.clientApp = clientApp;
        myDocumentsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {  
              if (myDocumentsList.getSelectedValue() != null) {
                open_btn.setEnabled(true);
                delete_btn.setEnabled(true);
                share_btn.setEnabled(true);
                sharedWithMeList.clearSelection();
                rename_btn.setEnabled(true);
               }
            }
           
        });
        sharedWithMeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {  
              if (sharedWithMeList.getSelectedValue() != null) {
                open_btn.setEnabled(true);
                delete_btn.setEnabled(false);
                share_btn.setEnabled(false);
                myDocumentsList.clearSelection();
                rename_btn.setEnabled(false);
               }
            }
           
        });
        sharedWithMeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof String) {
                    // Here value will be of the Type 'String'
                    ((JLabel) renderer).setText(((String) value).split("/")[1]);
                }
                return renderer;
            }
        });
        myDocumentsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof String) {
                    // Here value will be of the Type 'String'
                    ((JLabel) renderer).setText(((String) value).split("/")[1]);
                }
                return renderer;
            }
        });
        disableButtons();
    }

    public void setMyDocumentsList(String[] myDocs){
        myDocumentsList.setListData(myDocs);
        
    }
    
    public void setSharedList(String[] sharedList){
        sharedWithMeList.setListData(sharedList);
    }

    public void disableButtons(){
        open_btn.setEnabled(false);
        delete_btn.setEnabled(false);
        share_btn.setEnabled(false);
        rename_btn.setEnabled(false);
    }

    public void clearSelectionOfLists(){
        myDocumentsList.clearSelection();
        sharedWithMeList.clearSelection();
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
        delete_btn = new javax.swing.JButton();
        new_btn = new javax.swing.JButton();
        open_btn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        logout_btn = new javax.swing.JButton();
        rename_btn = new javax.swing.JButton();
        share_btn = new javax.swing.JButton();
        sharedWithMe = new javax.swing.JScrollPane();
        sharedWithMeList = new javax.swing.JList<>();
        myDocuments = new javax.swing.JScrollPane();
        myDocumentsList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 102, 51));

        delete_btn.setBackground(new java.awt.Color(255, 255, 204));
        delete_btn.setText("Delete");
        delete_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                delete_btnMouseClicked(evt);
            }
        });

        new_btn.setBackground(new java.awt.Color(255, 255, 204));
        new_btn.setText("New");
        new_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new_btnMouseClicked(evt);
            }
        });

        open_btn.setBackground(new java.awt.Color(255, 255, 204));
        open_btn.setText("Open");
        open_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                open_btnMouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Alien Encounters", 1, 30)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("REMOTE DOCS");

        logout_btn.setBackground(new java.awt.Color(255, 255, 204));
        logout_btn.setText("Logout");
        logout_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logout_btnMouseClicked(evt);
            }
        });

        rename_btn.setBackground(new java.awt.Color(255, 255, 204));
        rename_btn.setText("Rename");
        rename_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rename_btnMouseClicked(evt);
            }
        });

        share_btn.setBackground(new java.awt.Color(255, 255, 204));
        share_btn.setText("Share");
        share_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                share_btnMouseClicked(evt);
            }
        });

        sharedWithMe.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Shared with me", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        sharedWithMe.setViewportView(sharedWithMeList);

        myDocuments.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "My Documents", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        myDocuments.setViewportView(myDocumentsList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(new_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(share_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rename_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delete_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(open_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(myDocuments, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(sharedWithMe, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(logout_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(new_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(open_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(rename_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(delete_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(myDocuments)
                            .addComponent(sharedWithMe, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(share_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logout_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void delete_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_delete_btnMouseClicked
        if(delete_btn.isEnabled()) {
            String selectedValue = myDocumentsList.getSelectedValue();
            if (!selectedValue.isEmpty()) {
                int id = Integer.parseInt(selectedValue.split("/")[0]);
                DeleteFileRequest deleteFileRequest = DeleteFileRequest.newBuilder().setId(id).setUsername(clientApp.getUsername()).setToken(clientApp.getToken()).build();
                try {
                   clientApp.getFrontend().deleteFile(deleteFileRequest);
                   clientApp.deleteFile(id);
                   setMyDocumentsList(clientApp.getMyDocs());
                   disableButtons();
                }
                catch (StatusRuntimeException e) {
                    System.out.println("Caught exception with description: " +
                    e.getStatus().getDescription());
                    JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
                }
             }
        }
       
        
    }//GEN-LAST:event_delete_btnMouseClicked

    private void new_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_new_btnMouseClicked
        String filename = JOptionPane.showInputDialog(this, "Insert filename: ","New file",JOptionPane.PLAIN_MESSAGE);
        if (filename != null){
            CreateFileRequest createFileRequest = CreateFileRequest.newBuilder().setName(filename).setUsername(clientApp.getUsername()).setToken(clientApp.getToken()).build();
            try {
                CreateFileResponse createFileResponse = clientApp.getFrontend().createFile(createFileRequest);
                int id = createFileResponse.getId();
                LocalDateTime timestamp = Instant.ofEpochSecond(createFileResponse.getCreationTime().getSeconds()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                String username = clientApp.getUsername();
                clientApp.addFile(new FileDetails(id,filename,0,timestamp,username,username));
                setMyDocumentsList(clientApp.getMyDocs());
                EditDocumentForm editDocForm = clientApp.getEditdoc();
                editDocForm.setId(id);
                editDocForm.setLastUpdater(username);
                editDocForm.setOwner(username);
                editDocForm.setDateChange(timestamp);   
                editDocForm.setTitle(filename);    
                editDocForm.setPaneContent("");
                clientApp.switchForm(this, editDocForm);
            }
            catch (StatusRuntimeException e) {
                System.out.println("Caught exception with description: " +
                e.getStatus().getDescription());
                JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
            }
        }
        
    }//GEN-LAST:event_new_btnMouseClicked

    private void open_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_open_btnMouseClicked
        if(open_btn.isEnabled()){
            String selectedValue;
            if(!myDocumentsList.isSelectionEmpty())
                selectedValue = myDocumentsList.getSelectedValue();
            else
                selectedValue = sharedWithMeList.getSelectedValue();
                
            if (!selectedValue.isEmpty()) {
                int id = Integer.parseInt(selectedValue.split("/")[0]);
    
                DownloadRequest downloadRequest = DownloadRequest.newBuilder().setId(id).setUsername(clientApp.getUsername()).setToken(clientApp.getToken()).build();
                try {
                    DownloadResponse downloadResponse = clientApp.getFrontend().download(downloadRequest);
                    EditDocumentForm editDocForm = clientApp.getEditdoc();
                    String content = downloadResponse.getContent().toStringUtf8();
                    editDocForm.setPaneContent(content);
                    editDocForm.setId(id);
            
                    FileDetails details = clientApp.getFile(id);
                    
                    details.setContent(downloadResponse.getContent().toByteArray());
                    LocalDateTime timestamp = Instant.ofEpochSecond(downloadResponse.getLastChange().getSeconds()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    
                    editDocForm.setLastUpdater(downloadResponse.getLastUpdater());
                    editDocForm.setOwner(downloadResponse.getOwner());
                    editDocForm.setDateChange(timestamp);   
                    editDocForm.setTitle(details.getName());  
                    if(details.getPermission() == 1)
                        editDocForm.setViewerLayout(); 
                    else
                        editDocForm.setDefaultLayout();
                    clientApp.switchForm(this, editDocForm);
                }
                catch (StatusRuntimeException e) {
                    if(e.getStatus().getDescription().equals("You do not have enough permissions to access this file.")){
                       try {
                            GetDocumentsRequest docsRequest = GetDocumentsRequest.newBuilder().setUsername(clientApp.getUsername()).setToken(clientApp.getToken()).build();
                            GetDocumentsResponse docsResponse = clientApp.getFrontend().getDocumentsList(docsRequest);
                            List<DocumentInfo> listGrpc = docsResponse.getDocumentsList();
                            Map<Integer,FileDetails> listDocs = new TreeMap<>(); 
                            
                            for (DocumentInfo docGrpc: listGrpc){
                                listDocs.put(docGrpc.getId(), new FileDetails(docGrpc.getId(), docGrpc.getName(), docGrpc.getRelationship()));
                            }
                            JOptionPane.showMessageDialog(null, "This file is no longer shared with you");
    
                            clientApp.setFiles(listDocs);
                            setMyDocumentsList(clientApp.getMyDocs());
                            setSharedList(clientApp.getSharedWithMe());
                        }catch (StatusRuntimeException b) {
                            System.out.println("Caught exception with description: " +
                            e.getStatus().getDescription());
                            JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
                    
                       }
                                         
                    }
                    else{
                        System.out.println("Caught exception with description: " +
                        e.getStatus().getDescription());
                        JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
                    }
                }
            }
        
        }
       
        
    }//GEN-LAST:event_open_btnMouseClicked

    private void logout_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logout_btnMouseClicked
        String[] empty = {};
        clientApp.setFiles(new TreeMap<>());
        setMyDocumentsList(empty);
        setSharedList(empty);
        disableButtons();
        LogoutRequest logoutRequest = LogoutRequest.newBuilder().setUsername(clientApp.getUsername()).setToken(clientApp.getToken()).build();
        try {
            clientApp.getFrontend().logout(logoutRequest);
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " +
            e.getStatus().getDescription());
        }
        clientApp.switchForm(this, clientApp.getMenu());
    }//GEN-LAST:event_logout_btnMouseClicked

    private void rename_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rename_btnMouseClicked
        if(rename_btn.isEnabled()) {
            String filename = JOptionPane.showInputDialog(this, "Insert a new filename: ","New filename",JOptionPane.PLAIN_MESSAGE);
            if (filename!=null){
               String selectedValue = myDocumentsList.getSelectedValue();
               int index = myDocumentsList.getSelectedIndex();
               if (!selectedValue.isEmpty()) {
                  int id = Integer.parseInt(selectedValue.split("/")[0]);
                  UpdateFileNameRequest updateFileNameRequest = UpdateFileNameRequest.newBuilder().setId(id).setName(filename).setUsername(clientApp.getUsername()).setToken(clientApp.getToken()).build();
                  try {
                     clientApp.getFrontend().updateFileName(updateFileNameRequest);
                     clientApp.updateFileName(id, filename);
                     setMyDocumentsList(clientApp.getMyDocs());
                     myDocumentsList.setSelectedIndex(index);
                  }
                  catch (StatusRuntimeException e) {
                      System.out.println("Caught exception with description: " +
                      e.getStatus().getDescription());
                      JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
                  }
               }
            }
        }
         
        
       
    }//GEN-LAST:event_rename_btnMouseClicked

    private void share_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_share_btnMouseClicked
        if(share_btn.isEnabled()){
            String selectedValue = myDocumentsList.getSelectedValue();
            if (!selectedValue.isEmpty()) {
                int id = Integer.parseInt(selectedValue.split("/")[0]);
                SharedDocUsersRequest request = SharedDocUsersRequest.newBuilder()
                .setUsername(clientApp.getUsername())
                .setId(id)
                .setToken(clientApp.getToken()).build();
                try{
                    SharedDocUsersResponse response = clientApp.getFrontend().docUsersList(request);
                    List<UserGrpc> listGrpc = response.getUsersList();
                    ArrayList<String> users = new ArrayList<>();
                    for (UserGrpc userGrpc: listGrpc){
                        users.add(userGrpc.getPermission()+"/"+userGrpc.getUsername());
                    }
                    ShareForm shareForm = clientApp.getShare();
                    shareForm.setUsersList(users);
                    String filename = clientApp.getFile(id).getName();
                    shareForm.setFileNameTf(filename);
                    shareForm.setId(id);
                    clientApp.switchForm(this, shareForm);
    
                }catch (StatusRuntimeException e) {
                    System.out.println("Caught exception with description: " +
                    e.getStatus().getDescription());
                    JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
                }
            }
        }
       
               
        
    }//GEN-LAST:event_share_btnMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.dispose();
        clientApp.getFrontend().channelEnd();
        System.exit(0);   
     }//GEN-LAST:event_formWindowClosing
    
    

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton delete_btn;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton logout_btn;
    private javax.swing.JScrollPane myDocuments;
    private javax.swing.JList<String> myDocumentsList;
    private javax.swing.JButton new_btn;
    private javax.swing.JButton open_btn;
    private javax.swing.JButton rename_btn;
    private javax.swing.JButton share_btn;
    private javax.swing.JScrollPane sharedWithMe;
    private javax.swing.JList<String> sharedWithMeList;
    // End of variables declaration//GEN-END:variables
}
