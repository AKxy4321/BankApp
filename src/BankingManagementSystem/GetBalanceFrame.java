// GetBalanceFrame.java
package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetBalanceFrame extends JFrame {
    private Connection connection;
    private String email;
    private JTextField pinField;

    public GetBalanceFrame(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        setTitle("Check Balance");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JLabel pinLabel = new JLabel("Security Pin:");

        pinField = new JTextField();

        panel.add(pinLabel);
        panel.add(pinField);

        JButton checkBalanceButton = new JButton("Check Balance");
        JButton backButton = new JButton("Back");

        panel.add(checkBalanceButton);
        panel.add(backButton);

        add(panel);

        checkBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBalance();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AccountOperationsFrame(connection, email).setVisible(true);
            }
        });
    }

    private void checkBalance() {
        String pin = pinField.getText();

        String balanceQuery = "SELECT balance FROM Accounts WHERE email = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(balanceQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                JOptionPane.showMessageDialog(this, "Your Balance is: " + balance);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Security Pin!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
