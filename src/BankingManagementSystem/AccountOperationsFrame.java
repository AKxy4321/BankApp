// AccountOperationsFrame.java
package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Scanner;

public class AccountOperationsFrame extends JFrame {
    private Connection connection;
    private String email;

    public AccountOperationsFrame(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        setTitle("Account Operations");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JButton withdrawButton = new JButton("Withdraw Money");
        JButton depositButton = new JButton("Deposit Money");
        JButton transferButton = new JButton("Transfer Money");
        JButton getBalanceButton = new JButton("Check Balance");
        JButton logoutButton = new JButton("Log Out");

        panel.add(withdrawButton);
        panel.add(depositButton);
        panel.add(transferButton);
        panel.add(getBalanceButton);
        panel.add(logoutButton);

        add(panel);

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new WithdrawFrame(connection, email).setVisible(true);
            }
        });

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DepositFrame(connection, email).setVisible(true);
            }
        });

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TransferFrame(connection, email).setVisible(true);
            }
        });

        getBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GetBalanceFrame(connection, email).setVisible(true);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainFrame(connection, new Scanner(System.in)).setVisible(true);
            }
        });
    }
}
