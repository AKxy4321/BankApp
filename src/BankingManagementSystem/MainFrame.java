package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class MainFrame extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/BankApp";
    private static final String username = "akxy4321";
    private static final String password = "1234";
    private Connection connection;
    private Scanner scanner;

    public MainFrame(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;

        setTitle("Banking Management System");
        setSize(400, 300);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        panel.add(new JLabel("*** WELCOME TO BANKING SYSTEM ***"));
        panel.add(registerButton);
        panel.add(loginButton);
        panel.add(exitButton);

        add(panel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new RegisterFrame(connection, scanner);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new LoginFrame(connection, scanner);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {if(connection != null)
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) throws SQLException {
        // Assuming connection and scanner are created here
        Connection connection = DriverManager.getConnection(url, username, password);
        Scanner scanner = new Scanner(System.in);
        new MainFrame(connection, scanner).setVisible(true);
    }
}
