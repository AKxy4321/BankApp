// DepositFrame.java
package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepositFrame extends JFrame {
    private Connection connection;
    private String email;
    private JTextField amountField;
    private JTextField pinField;

    public DepositFrame(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        setTitle("Deposit Money");
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

        JButton depositButton = new JButton("Deposit");
        JButton backButton = new JButton("Back");

        panel.add(depositButton);
        panel.add(backButton);

        add(panel);

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deposit();
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

    private void deposit() {
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

            String depositQuery = "UPDATE Accounts SET balance = balance + ? WHERE email = ? AND security_pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(depositQuery);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, pin);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Deposit Successful!");
                dispose();
                new AccountOperationsFrame(connection, email).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Deposit Failed! Invalid Pin.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
