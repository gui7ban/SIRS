package sirs.remotedocs;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Frame;
import java.awt.Point;
public class CustomDialog extends JDialog implements ActionListener {
    private String[] data;
    private JTextField descBox;
    private JComboBox<String> colorList;
    private JButton btnOk;
    private JButton btnCancel;
    public CustomDialog(Frame parent) {
        super(parent,"Insert User",true);
        setResizable(false); 
        Point loc = parent.getLocation();
        setLocation(loc.x+60,loc.y+60);
        data = new String[2]; // set to amount of data items
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        JLabel descLabel = new JLabel("Username:");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(descLabel,gbc);
        descBox = new JTextField(30);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(descBox,gbc);
        JLabel colorLabel = new JLabel("Choose permission:");
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(colorLabel,gbc);
        String[] colorStrings = {"editor","viewer"};
        colorList = new JComboBox<String>(colorStrings);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(colorList,gbc);
        JLabel spacer = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(spacer,gbc);
        btnOk = new JButton("Add");
        btnOk.addActionListener(this);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(btnOk,gbc);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(btnCancel,gbc);
        getContentPane().add(panel);
        pack();
   }
   public void actionPerformed(ActionEvent ae) {
      Object source = ae.getSource();
      if (source == btnOk) {
         data[0] = descBox.getText();
         data[1] = (String)colorList.getSelectedItem();
      }
      else {
         data[0] = null;
      }
      dispose();
   }
   public String[] run() {
      this.setVisible(true);
      return data;
   }
}
