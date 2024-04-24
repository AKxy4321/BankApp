// WithdrawFrame.java
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

public class WithdrawFrame extends JFrame {
    private Connection connection;
    private String email;
    private JTextField amountField;
    private JTextField pinField;

    public WithdrawFrame(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        setTitle("Withdraw Money");
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel amountLabel = new JLabel("Amount:");
        JLabel pinLabel = new JLabel("Security Pin:");

        amountField = new JTextField();
        pinField = new JTextField();

        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(pinLabel);
        panel.add(pinField);

        JButton withdrawButton = new JButton("Withdraw");
        JButton backButton = new JButton("Back");

        panel.add(withdrawButton);
        panel.add(backButton);

        add(panel);

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdraw();
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

    private void withdraw() {
        String amountText = amountField.getText();
        String pin = pinField.getText();

        // Check if amount and pin are not empty
        if (amountText.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter amount and pin.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive value.");
                return;
            }

            String balanceQuery = "SELECT balance FROM Accounts WHERE email = ? AND security_pin = ?";
            PreparedStatement balanceStatement = connection.prepareStatement(balanceQuery);
            balanceStatement.setString(1, email);
            balanceStatement.setString(2, pin);
            ResultSet balanceResultSet = balanceStatement.executeQuery();
            if (balanceResultSet.next()) {
                double balance = ((ResultSet) balanceResultSet).getDouble("balance");
                if (amount > balance) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                    return;
                }

                String withdrawQuery = "UPDATE Accounts SET balance = balance - ? WHERE email = ? AND security_pin = ?";
                PreparedStatement withdrawStatement = connection.prepareStatement(withdrawQuery);
                withdrawStatement.setDouble(1, amount);
                withdrawStatement.setString(2, email);
                withdrawStatement.setString(3, pin);
                int rowsAffected = withdrawStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Withdrawal Successful!");
                    dispose();
                    new AccountOperationsFrame(connection, email).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Withdrawal Failed! Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Security Pin!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
