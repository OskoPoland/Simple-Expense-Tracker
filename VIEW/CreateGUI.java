package VIEW;
import CONTROLLER.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreateGUI {
    JFrame frame = new JFrame();
    LogGUI log = new LogGUI();
    DataControls dc = new DataControls();

    public void createConstruct() {
        frame.setSize(350,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel basePanel = new JPanel(new BorderLayout(0, 0));
        frame.add(basePanel, BorderLayout.CENTER);
        basePanel.setLayout(new BorderLayout(0, 0));


        
        //Creating left panel with "username:" and "password:" titles
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        
        JLabel UString = new JLabel("Username:");
        GridBagConstraints gbc_UString = new GridBagConstraints();
        gbc_UString.anchor = gbc_UString.WEST;
        gbc_UString.gridx = 0;
        gbc_UString.gridy = 0;
        gbc_UString.insets = new Insets(0, 5, 15, 5);
        leftPanel.add(UString, gbc_UString);

        JLabel PString = new JLabel("Password:");
        GridBagConstraints gbc_PString = new GridBagConstraints();
        gbc_PString.anchor = gbc_PString.WEST;
        gbc_PString.gridx = 0;
        gbc_PString.gridy = 2;
        gbc_PString.insets = new Insets(0, 5, 15, 5);
        leftPanel.add(PString, gbc_PString);

        basePanel.add(leftPanel, BorderLayout.LINE_START);

        //Creating Center Panel with the fields for entry

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());

        JTextField UField = new JTextField();
        GridBagConstraints gbc_UField = new GridBagConstraints();
        gbc_UField.fill = gbc_UField.HORIZONTAL;
        gbc_UField.gridx = 1;
        gbc_UField.gridy = 0;
        gbc_UField.weightx = 1.0;
        gbc_UField.insets = new Insets(0, 5, 15, 5);
        centerPanel.add(UField, gbc_UField);

        JTextField PField = new JTextField();
        GridBagConstraints gbc_PField = new GridBagConstraints();
        gbc_PField.fill = gbc_PField.HORIZONTAL;
        gbc_PField.gridx = 1;
        gbc_PField.gridy = 2;
        gbc_PField.weightx = 1.0;
        gbc_PField.insets = new Insets(0, 5, 15, 5);
        centerPanel.add(PField, gbc_PField);

        basePanel.add(centerPanel, BorderLayout.CENTER);

        //Adding Panel for two buttons on the bottom of the screen
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());

        JButton cancelBut = new JButton("     Cancel    ");
        GridBagConstraints gbc_logBut = new GridBagConstraints();
        gbc_logBut.anchor = gbc_logBut.WEST;
        gbc_logBut.gridx = 0;
        gbc_logBut.gridy = 0;
        gbc_logBut.weighty = 1.0;
        gbc_logBut.insets = new Insets(0,5,20,15);
        bottomPanel.add(cancelBut, gbc_logBut);

        JButton createBut = new JButton("Create Account");
        GridBagConstraints gbc_createBut = new GridBagConstraints();
        gbc_createBut.anchor = gbc_createBut.WEST;
        gbc_createBut.gridx = 2;
        gbc_createBut.gridy = 0;
        gbc_createBut.weighty = 1.0;
        gbc_createBut.insets = new Insets(0,15,20,5);
        bottomPanel.add(createBut, gbc_createBut);

        basePanel.add(bottomPanel, BorderLayout.PAGE_END);

        //Create Panel for title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Enter a Username and Password");
        title.setFont(new Font("Arial", Font.BOLD, 15));
        GridBagConstraints gbc_title = new GridBagConstraints();
        gbc_title.anchor  = gbc_title.WEST;
        gbc_title.gridx = 1;
        gbc_title.gridy = 0;
        gbc_title.weighty = 1.0;
        gbc_title.insets = new Insets(30, 5, 5, 5);
        titlePanel.add(title, gbc_title);

        basePanel.add(titlePanel, BorderLayout.PAGE_START);

        createBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dc.createUser(UField.getText(), PField.getText());
                log.initLog();
            }
        });

        frame.add(basePanel, BorderLayout.CENTER);
    }

    public void createInit() {
        frame.setVisible(true);
    }


}
