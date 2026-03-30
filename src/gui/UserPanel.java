package gui;

import manager.UserManager;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// sadecc adminin görebileceği kisım
public class UserPanel extends JPanel {
    private UserManager userManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtUsername, txtPassword;
    private JComboBox<String> cbRole;

    public UserPanel(UserManager userManager) {
        this.userManager = userManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildTable();
        buildForm();
        refreshTable();
    }

    private void buildTable() {
        String[] cols = {"Kullanici Adi", "Rol"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Kullanici Listesi"));
        add(scroll, BorderLayout.CENTER);
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Yeni Kullanici Ekle"));
        form.setPreferredSize(new Dimension(240, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0; form.add(new JLabel("Kullanici Adi:"), gbc);
        gbc.gridy = 1; txtUsername = new JTextField(); form.add(txtUsername, gbc);
        gbc.gridy = 2; form.add(new JLabel("Sifre:"), gbc);
        gbc.gridy = 3; txtPassword = new JTextField(); form.add(txtPassword, gbc);
        gbc.gridy = 4; form.add(new JLabel("Rol:"), gbc);
        gbc.gridy = 5; cbRole = new JComboBox<>(new String[]{"ADMIN", "GARSON"});
        form.add(cbRole, gbc);

        gbc.gridy = 6;
        JButton btnAdd = new JButton("Ekle");
        btnAdd.setBackground(new Color(40, 150, 40));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> addUser());
        form.add(btnAdd, gbc);

        gbc.gridy = 7;
        JButton btnDel = new JButton("Sil");
        btnDel.setBackground(new Color(190, 40, 40));
        btnDel.setForeground(Color.WHITE);
        btnDel.setFont(new Font("Arial", Font.BOLD, 13));
        btnDel.setFocusPainted(false);
        btnDel.addActionListener(e -> deleteUser());
        form.add(btnDel, gbc);

        add(form, BorderLayout.EAST);
    }

    private void addUser() {
        String uname = txtUsername.getText().trim();
        String pwd   = txtPassword.getText().trim();
        String role  = (String) cbRole.getSelectedItem();
        if (uname.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kullanici adi ve sifre bos olamaz!");
            return;
        }
        if (userManager.usernameExists(uname)) {
            JOptionPane.showMessageDialog(this, "Bu kullanici adi zaten mevcut!");
            return;
        }
        userManager.addUser(new User(uname, pwd, role));
        refreshTable();
        txtUsername.setText("");
        txtPassword.setText("");
        JOptionPane.showMessageDialog(this, "Kullanici eklendi.");
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Silmek icin bir kullanici seciniz!");
            return;
        }
        String uname = (String) tableModel.getValueAt(row, 0);
        if ("admin".equals(uname)) {
            JOptionPane.showMessageDialog(this, "Varsayilan admin silinemez!");
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
                uname + " kullanicisi silinsin mi?", "Sil", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            userManager.deleteUser(uname);
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (User u : userManager.getAll()) {
            tableModel.addRow(new Object[]{u.getUsername(), u.getRole()});
        }
    }
}
