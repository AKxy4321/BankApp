// LoginFrame.java
package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LoginFrame extends JFrame {
    private Connection connection;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame(Connection connection, Scanner scanner) {
        this.connection = connection;

        setTitle("Login");
        setSize(400, 200);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        emailField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        panel.add(loginButton);
        panel.add(backButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new MainFrame(connection, scanner).setVisible(true);
            }
        });
        setVisible(true);
    }

    private void login() {
        String email = emailField.getText();
        String password = String.valueOf(passwordField.getPassword());

        // SQL query to check if the email exists in the User table
        String checkEmailQuery = "SELECT COUNT(*) FROM User WHERE email = ?";
        try {
            PreparedStatement checkEmailStatement = connection.prepareStatement(checkEmailQuery);
            checkEmailStatement.setString(1, email);
            ResultSet checkEmailResult = checkEmailStatement.executeQuery();
            checkEmailResult.next();
            int count = checkEmailResult.getInt(1);

            if (count > 0) {
                // If email exists, proceed with login
                String loginQuery = "SELECT * FROM User WHERE email = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");

                    // Check if a banking account exists
                    String checkAccountQuery = "SELECT COUNT(*) FROM Accounts WHERE email = ?";
                    PreparedStatement checkAccountStatement = connection.prepareStatement(checkAccountQuery);
                    checkAccountStatement.setString(1, email);
                    ResultSet checkAccountResult = checkAccountStatement.executeQuery();
                    checkAccountResult.next();
                    int accountCount = checkAccountResult.getInt(1);

                    if (accountCount == 0) {
                        // If banking account doesn't exist, prompt to create one
                        int option = JOptionPane.showConfirmDialog(this, "No banking account found. Do you want to create one?",
                                "Account Not Found", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            new AccountCreationFrame(connection, email).setVisible(true);
                        }
                    } else {
                        // Banking account exists, proceed to account operations
                        dispose();
                        new AccountOperationsFrame(connection, email).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect Password!");
                }
            } else {
                // If email does not exist, prompt to create an account
                int option = JOptionPane.showConfirmDialog(this, "Account doesn't exist. Do you want to create one?",
                        "Account Not Found", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    dispose(); // Close login frame
                    // Creating and showing AccountCreationFrame
                    SwingUtilities.invokeLater(() -> new AccountCreationFrame(connection, email).setVisible(true));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}
