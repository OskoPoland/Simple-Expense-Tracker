package VIEW;
import CONTROLLER.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ExpenseGUI {
    JFrame frame = new JFrame();
    DataControls dc = new DataControls();

    public void expenseConstruct() {
        //Frame configurations
        frame.setSize(500,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBackground(Color.GRAY);

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());

        //Option Buttons subpanels
        JPanel btnSubPanel = new JPanel();
        btnSubPanel.setLayout(new GridBagLayout());
        
        JButton homeBtn = new JButton("HOME");
        GridBagConstraints gbc_homeBtn = new GridBagConstraints();
        gbc_homeBtn.gridx = 0;
        gbc_homeBtn.gridy = 0;
        gbc_homeBtn.insets = new Insets(15, 15, 15, 15);
        homeBtn.setBackground(Color.BLACK);
        homeBtn.setForeground(Color.GRAY);
        btnSubPanel.add(homeBtn, gbc_homeBtn);

        JButton addExpBtn = new JButton("ADD EXPENSE");
        GridBagConstraints gbc_addExpBtn = new GridBagConstraints();
        gbc_addExpBtn.gridx = 0;
        gbc_addExpBtn.gridy = 2;
        gbc_addExpBtn.insets = new Insets(15, 15, 15, 15);
        addExpBtn.setBackground(Color.BLACK);
        addExpBtn.setForeground(Color.GRAY);
        btnSubPanel.add(addExpBtn, gbc_addExpBtn);

        JButton delExpBtn = new JButton("DELETE EXPENSE");
        GridBagConstraints gbc_delExpBtn = new GridBagConstraints();
        gbc_delExpBtn.gridx = 0;
        gbc_delExpBtn.gridy = 4;
        gbc_delExpBtn.insets = new Insets(15, 15, 15, 15);
        delExpBtn.setBackground(Color.BLACK);
        delExpBtn.setForeground(Color.GRAY);

        JButton settingsBtn = new JButton("SETTINGS");
        GridBagConstraints gbc_settingsBtn = new GridBagConstraints();
        gbc_settingsBtn.gridx = 0;
        gbc_settingsBtn.gridy = 6;
        gbc_settingsBtn.insets = new Insets(15, 15, 15, 15);
        settingsBtn.setBackground(Color.BLACK);
        settingsBtn.setForeground(Color.GRAY);
        btnSubPanel.add(settingsBtn, gbc_settingsBtn);

        basePanel.add(btnSubPanel, BorderLayout.PAGE_START);

        //Expense Entry Panel. When button pressed will overlay the chart until entry is finished
        // - labels and fields for entry
        // - combo box for categories
        JPanel expenseEntryPanel = new JPanel();
        expenseEntryPanel.setLayout(new GridBagLayout());

        JLabel expName = new JLabel("Expense Name:");
        JTextField expNameEntry = new JTextField();
        JLabel catName = new JLabel("Select Expense:");
        JComboBox catSelect = new JComboBox(dc.getCategories(dc.getID()));
        


        //JCHART in BorderLayout.CENTER area below
        JPanel mainChartPanel = new JPanel();
        mainChartPanel.setLayout(new BorderLayout());

        //chart title panel
        JPanel subMainChartTitlePanel = new JPanel();
        subMainChartTitlePanel.setLayout(new GridBagLayout());

        //Sub Panel for just the chart
        JPanel subMainChartPanel = new JPanel();
            //Layout not really known yet

        //Expense Breakdown Panel
        JPanel brkDownPanel = new JPanel();
        brkDownPanel.setLayout(new GridBagLayout());

        
        //Button Action Listeners
        homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainChartPanel.setVisible(true);
                brkDownPanel.setVisible(true);
            }
        });

        settingsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //Dont have a settings page yet
            }
        });

        addExpBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        
        delExpBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        editExpBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
