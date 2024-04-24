// AccountCreationFrame.java
package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountCreationFrame extends JFrame {
    private Connection connection;
    private String email;
    private JTextField fullNameField;
    private JTextField pinField;
    private JTextField balanceField; // New field for balance

    public AccountCreationFrame(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        setTitle("Create Account");
        setSize(400, 250); // Increased height to accommodate the balance field
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2)); // Adjusted for the new balance field

        JLabel fullNameLabel = new JLabel("Full Name:");
        JLabel pinLabel = new JLabel("Security Pin:");
        JLabel balanceLabel = new JLabel("Initial Balance:"); // Label for balance

        fullNameField = new JTextField();
        pinField = new JTextField();
        balanceField = new JTextField(); // Added balance field

        panel.add(fullNameLabel);
        panel.add(fullNameField);
        panel.add(pinLabel);
        panel.add(pinField);
        panel.add(balanceLabel); // Added balance label
        panel.add(balanceField); // Added balance field

        JButton createAccountButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");

        panel.add(createAccountButton);
        panel.add(backButton);

        add(panel);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainFrame(connection, new Scanner(System.in)).setVisible(true);
            }
        });
    }

    private void createAccount() {
        String fullName = fullNameField.getText();
        String pin = pinField.getText();
        double balance = Double.parseDouble(balanceField.getText()); // Parse balance as double

        String createAccountQuery = "INSERT INTO Accounts(full_name, email, security_pin, balance) VALUES(?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(createAccountQuery);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, pin);
            preparedStatement.setDouble(4, balance); // Set balance parameter
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Account Created Successfully!");
                dispose();
                new AccountOperationsFrame(connection, email).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Account Creation Failed!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance format!");
        }
    }
}
