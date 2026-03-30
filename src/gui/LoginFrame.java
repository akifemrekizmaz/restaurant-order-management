package gui;

import manager.UserManager;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


// giris icin: admin veya garson1 sifre: 1234

public class LoginFrame extends JFrame {
    private UserManager userManager;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginFrame() {
        this.userManager = new UserManager();
        initComponents();
    }

    private void initComponents() {
        setTitle("Restoran Siparis Sistemi - Giris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(38, 38, 38));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel lblTitle = new JLabel("RESTORAN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(new Color(255, 165, 0));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Siparis Yonetim Sistemi", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(new Color(180, 180, 180));
        gbc.gridy = 1;
        mainPanel.add(lblSub, gbc);


        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 80, 80));
        gbc.gridy = 2;
        mainPanel.add(sep, gbc);


        gbc.gridwidth = 1; gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblUser = new JLabel("Kullanici Adi:");
        lblUser.setForeground(Color.WHITE);
        mainPanel.add(lblUser, gbc);

        txtUsername = new JTextField(16);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.weightx = 1;
        mainPanel.add(txtUsername, gbc);


        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel lblPass = new JLabel("Sifre:");
        lblPass.setForeground(Color.WHITE);
        mainPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(16);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.weightx = 1;
        mainPanel.add(txtPassword, gbc);

        
        btnLogin = new JButton("GIRIS YAP");
        btnLogin.setBackground(new Color(255, 140, 0));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 15));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 38));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weightx = 1;
        mainPanel.add(btnLogin, gbc);


        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setForeground(new Color(255, 80, 80));
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridy = 6;
        mainPanel.add(lblStatus, gbc);


        JLabel lblHint = new JLabel("Varsayilan: admin/1234 | garson1/1234", SwingConstants.CENTER);
        lblHint.setForeground(new Color(120, 120, 120));
        lblHint.setFont(new Font("Arial", Font.PLAIN, 11));
        gbc.gridy = 7;
        mainPanel.add(lblHint, gbc);

        add(mainPanel);


        btnLogin.addActionListener(e -> performLogin());

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtUsername.requestFocusInWindow();
            }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Kullanici adi ve sifre bos olamaz!");
            return;
        }

        User user = userManager.login(username, password);
        if (user != null) {
            dispose(); 
            new MainFrame(user, userManager).setVisible(true);
        } else {
            lblStatus.setText("Hatali kullanici adi veya sifre!");
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }
}
