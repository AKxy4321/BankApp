// RegisterFrame.java
package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class RegisterFrame extends JFrame {
    private Connection connection;
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegisterFrame(Connection connection, Scanner scanner) {
        this.connection = connection;

        setTitle("Register");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel fullNameLabel = new JLabel("Full Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        fullNameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(fullNameLabel);
        panel.add(fullNameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        panel.add(registerButton);
        panel.add(backButton);

        add(panel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
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

    private void register() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = String.valueOf(passwordField.getPassword());

        String registerQuery = "INSERT INTO User(full_name, email, password) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                dispose();
                new MainFrame(connection, new Scanner(System.in)).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
