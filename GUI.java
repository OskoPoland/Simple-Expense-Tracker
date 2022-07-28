import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    JFrame frame = new JFrame();

    public GUI() {
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //Login Text Field
        JLabel logU = new JLabel("Username:");
        c.gridx = 0;
        c.gridy = 1;
        panel.add(logU, c);

        JLabel logP = new JLabel("Enter your password");
        c.gridx = 0;
        c.gridy = 2;
        panel.add(logP, c);

        JLabel logTitle = new JLabel("Welcome to Arye's Expense Manager");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        panel.add(logTitle, c);
        
        //Text Entry Fields
        JTextField username = new JTextField();
        JTextField password = new JTextField();

        //Login Buttons
        JButton logBut = new JButton("Login");
        c.gridx = 2;
        c.gridy = 1;
        panel.add(logBut, c);

        JButton createBut = new JButton("Create a new account");
        c.gridx = 2;
        c.gridy = 2;
        panel.add(createBut, c);

        //Button Action Listeners
        logBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });

    }
}
