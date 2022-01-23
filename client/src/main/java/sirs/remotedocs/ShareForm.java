/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sirs.remotedocs;

import java.util.List;

import java.awt.Component;

import javax.swing.JList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import io.grpc.StatusRuntimeException;
import sirs.remotedocs.grpc.Contract.*;

/**
 *
 * @author tomaz
 */
public class ShareForm extends javax.swing.JFrame {
    private ClientApp clientApp;
    private int id;
    /**
     * Creates new form shareForm
     * @param clientApp
     * @param id
     */
    public ShareForm(ClientApp clientApp) {
        initComponents();
        this.clientApp = clientApp;
        sharedWithList.setCellRenderer(new DefaultListCellRenderer() {
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

        sharedWithList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {  
              if (sharedWithList.getSelectedValue() != null) {
                manage_btn.setEnabled(true);
                remove_btn.setEnabled(true);
               }
            }
           
        });
        
    }

    public void setUsersList(List<String> users){
        DefaultListModel<String> model = (DefaultListModel<String>)sharedWithList.getModel();
        model.clear();
        model.addAll(users);
        if (users.size()==0){
            remove_btn.setEnabled(false);
            manage_btn.setEnabled(false);
        }
        else
            sharedWithList.setSelectedIndex(0);
    }

    public void setFileNameTf(String filename){
        filename_tf.setText(filename);
    }

    public void setId(int id){
        this.id = id;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        sharedWithList = new javax.swing.JList<>(new DefaultListModel<String>());
        jLabel1 = new javax.swing.JLabel();
        filename_tf = new javax.swing.JTextField();
        manage_btn = new javax.swing.JButton();
        back_btn = new javax.swing.JButton();
        remove_btn = new javax.swing.JButton();
        add_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 102, 51));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Shared with:", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jScrollPane1.setViewportView(sharedWithList);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Filename: ");

        filename_tf.setEditable(false);

        manage_btn.setBackground(new java.awt.Color(255, 255, 204));
        manage_btn.setText("Manage");
        manage_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                manage_btnMouseClicked(evt);
            }
        });

        back_btn.setBackground(new java.awt.Color(255, 255, 204));
        back_btn.setText("Back");
        back_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                back_btnMouseClicked(evt);
            }
        });

        remove_btn.setBackground(new java.awt.Color(255, 255, 204));
        remove_btn.setText("Remove");
        remove_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                remove_btnMouseClicked(evt);
            }
        });

        add_btn.setBackground(new java.awt.Color(255, 255, 204));
        add_btn.setText("Add");
        add_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                add_btnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filename_tf))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(manage_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(back_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(remove_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(add_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(filename_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(manage_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(remove_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(add_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(back_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void back_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back_btnMouseClicked
        clientApp.switchForm(this, clientApp.getDoclist());
    }//GEN-LAST:event_back_btnMouseClicked

    private void remove_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_remove_btnMouseClicked
        String selectedValue = sharedWithList.getSelectedValue();
        int index = sharedWithList.getSelectedIndex();
        if (index != -1)
        {
            String[] split = selectedValue.split("/");
            String username = split[1];
            DeletePermissionUserRequest request = DeletePermissionUserRequest.newBuilder()
            .setOwner(clientApp.getUsername())
            .setId(this.id)
            .setUsername(username)
            .setToken(clientApp.getToken()).build();
            try {
                clientApp.getFrontend().deletePermission(request);
                DefaultListModel<String> model = (DefaultListModel<String>)sharedWithList.getModel();                
                model.remove(index);
                if (index > 0)
                    sharedWithList.setSelectedIndex(index-1); 
                else if (model.getSize() > 0)
                    sharedWithList.setSelectedIndex(0);
                else {
                    remove_btn.setEnabled(false);
                    manage_btn.setEnabled(false);
                }


            
            }catch (StatusRuntimeException e) {
                System.out.println("Caught exception with description: " +
                e.getStatus().getDescription());
                JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
            }
        }
    }//GEN-LAST:event_remove_btnMouseClicked

    private void add_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_add_btnMouseClicked
        String[] result = new CustomDialog(this).run();
        String username = result[0];
        String permission = result[1];
        int newPermission;
        if (username != null){
            if (permission.equals("editor"))
                newPermission = 2;
            else   
                newPermission = 1;
            AddPermissionUserRequest request = AddPermissionUserRequest.newBuilder()
            .setOwner(clientApp.getUsername())
            .setId(this.id)
            .setPermission(newPermission)
            .setUsername(username)
            .setToken(clientApp.getToken()).build();
            try {
                clientApp.getFrontend().addPermission(request);
                DefaultListModel<String> model = (DefaultListModel<String>)sharedWithList.getModel();                
                model.addElement(newPermission+"/"+username);
                sharedWithList.setSelectedIndex(model.size()-1);
            
            }catch (StatusRuntimeException e) {
                System.out.println("Caught exception with description: " +
                e.getStatus().getDescription());
                JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
            }
        }
    }//GEN-LAST:event_add_btnMouseClicked

    private void manage_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_manage_btnMouseClicked
        String selectedValue = sharedWithList.getSelectedValue();
        int index = sharedWithList.getSelectedIndex();
        if (!selectedValue.isEmpty()) {
            String[] split = selectedValue.split("/");
            int permission = Integer.parseInt(split[0]);
            String username = split[1];
            String permissionInText;
            if(permission == 1)
                permissionInText = "viewer";
            else
                permissionInText = "editor";
            Object[] possibilities = {"editor", "viewer"};

            String s = (String)JOptionPane.showInputDialog(this,
            "Choose Permissions:",
            "Manage Permissions",
            JOptionPane.PLAIN_MESSAGE,
            null,
            possibilities,
            permissionInText);
            if ((s != null) && (s.length() > 0)) {
                int newPermission;
                if (s.equals("editor"))
                    newPermission = 2;
                else   
                    newPermission = 1;
                if(permission != newPermission)
                {
                    UpdatePermissionUserRequest request = UpdatePermissionUserRequest.newBuilder()
                    .setOwner(clientApp.getUsername())
                    .setId(this.id)
                    .setPermission(newPermission)
                    .setUsername(username)
                    .setToken(clientApp.getToken()).build();
                    try {
                       clientApp.getFrontend().updatePermission(request);
                       DefaultListModel<String> model = (DefaultListModel<String>)sharedWithList.getModel();                
                        model.setElementAt(newPermission+"/"+username, index);
                    
                    }catch (StatusRuntimeException e) {
                        System.out.println("Caught exception with description: " +
                        e.getStatus().getDescription());
                        JOptionPane.showMessageDialog(null, e.getStatus().getDescription());
                    }

                    
                }
            }

        }

    }//GEN-LAST:event_manage_btnMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_btn;
    private javax.swing.JButton back_btn;
    private javax.swing.JTextField filename_tf;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton manage_btn;
    private javax.swing.JButton remove_btn;
    private javax.swing.JList<String> sharedWithList;
    // End of variables declaration//GEN-END:variables
}
