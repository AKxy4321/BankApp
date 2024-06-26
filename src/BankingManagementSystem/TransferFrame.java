// TransferFrame.java
package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferFrame extends JFrame {
    private Connection connection;
    private String email;
    private JTextField receiverAccountNumberField;
    private JTextField amountField;
    private JTextField pinField;

    public TransferFrame(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        setTitle("Transfer Money");
        setSize(400, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel receiverAccountNumberLabel = new JLabel("Receiver Account Number:");
        JLabel amountLabel = new JLabel("Amount:");
        JLabel pinLabel = new JLabel("Security Pin:");

        receiverAccountNumberField = new JTextField();
        amountField = new JTextField();
        pinField = new JTextField();

        panel.add(receiverAccountNumberLabel);
        panel.add(receiverAccountNumberField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(pinLabel);
        panel.add(pinField);

        JButton transferButton = new JButton("Transfer");
        JButton backButton = new JButton("Back");

        panel.add(transferButton);
        panel.add(backButton);

        add(panel);

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transfer();
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

    private void transfer() {
        String receiverAccountNumberText = receiverAccountNumberField.getText();
        String amountText = amountField.getText();
        String pin = pinField.getText();

        // Check if receiver account number, amount, and pin are not empty
        if (receiverAccountNumberText.isEmpty() || amountText.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter receiver account number, amount, and pin.");
            return;
        }

        try {
            long receiverAccountNumber = Long.parseLong(receiverAccountNumberText);
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive value.");
                return;
            }

            // Check if the user has sufficient balance
            String balanceQuery = "SELECT balance FROM Accounts WHERE email = ? AND security_pin = ?";
            PreparedStatement balanceStatement = connection.prepareStatement(balanceQuery);
            balanceStatement.setString(1, email);
            balanceStatement.setString(2, pin);
            ResultSet balanceResultSet = balanceStatement.executeQuery();
            if (balanceResultSet.next()) {
                double balance = balanceResultSet.getDouble("balance");
                if (amount > balance) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                    return;
                }

                try {
                    connection.setAutoCommit(false);

                    // Debit operation for sender
                    String debitQuery = "UPDATE Accounts SET balance = balance - ? WHERE email = ? AND security_pin = ?";
                    PreparedStatement debitPreparedStatement = connection.prepareStatement(debitQuery);
                    debitPreparedStatement.setDouble(1, amount);
                    debitPreparedStatement.setString(2, email);
                    debitPreparedStatement.setString(3, pin);
                    int rowsAffected1 = debitPreparedStatement.executeUpdate();

                    // Credit operation for receiver
                    String creditQuery = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement creditPreparedStatement = connection.prepareStatement(creditQuery);
                    creditPreparedStatement.setDouble(1, amount);
                    creditPreparedStatement.setLong(2, receiverAccountNumber);
                    int rowsAffected2 = creditPreparedStatement.executeUpdate();

                    if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                        JOptionPane.showMessageDialog(this, "Transaction Successful! Rs." + amount + " Transferred Successfully");
                        connection.commit();
                        dispose();
                        new AccountOperationsFrame(connection, email).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Transaction Failed");
                        connection.rollback();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    connection.setAutoCommit(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Security Pin!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid receiver account number or amount. Please enter valid numbers.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
