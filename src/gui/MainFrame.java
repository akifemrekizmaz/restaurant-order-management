package gui;

import manager.MenuManager;
import manager.OrderManager;
import manager.UserManager;
import model.User;
import javax.swing.*;
import java.awt.*;


 //ADMIN: tüm sekmeleri görür.
 //GARSON: sadece siparişler sekmesini görür.

public class MainFrame extends JFrame {
    private User currentUser;
    private MenuManager menuManager;
    private OrderManager orderManager;
    private UserManager userManager;

    public MainFrame(User user, UserManager userManager) {
        this.currentUser = user;
        this.userManager = userManager;
        this.menuManager = new MenuManager();
        this.orderManager = new OrderManager(menuManager);
        initComponents();
    }

    private void initComponents() {
        setTitle("Restoran Siparis Sistemi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));


        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 30, 30));
        topBar.setPreferredSize(new Dimension(0, 52));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(255, 140, 0)));

        JLabel lblLogo = new JLabel("  RESTORAN SIPARIS SISTEMI");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 18));
        lblLogo.setForeground(new Color(255, 165, 0));
        topBar.add(lblLogo, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        userPanel.setBackground(new Color(30, 30, 30));


        JLabel lblUserInfo = new JLabel(
                currentUser.getUsername().toUpperCase() + "  |  " + currentUser.getRole());
        lblUserInfo.setForeground(new Color(200, 200, 200));
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 13));
        userPanel.add(lblUserInfo);

        JButton btnLogout = new JButton("Cikis");
        btnLogout.setBackground(new Color(180, 40, 40));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> confirmLogout());
        userPanel.add(btnLogout);

        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);


        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Arial", Font.BOLD, 13));


        tabs.addTab("  Siparisler  ", new OrderPanel(orderManager, menuManager, currentUser));


        if ("ADMIN".equals(currentUser.getRole())) {
            tabs.addTab("  Menu Yonetimi  ", new MenuPanel(menuManager));
            tabs.addTab("  Istatistikler  ", new StatisticsPanel(orderManager, menuManager));
            tabs.addTab("  Kullanicilar  ", new UserPanel(userManager));
        }

        add(tabs, BorderLayout.CENTER);


        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        JLabel lblStatus = new JLabel("Hazir  |  Kullanici: " + currentUser.getUsername()
                + "  |  Rol: " + currentUser.getRole());
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        lblStatus.setForeground(Color.DARK_GRAY);
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void confirmLogout() {
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Cikis yapmak istediginize emin misiniz?",
                "Cikis",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (answer == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
