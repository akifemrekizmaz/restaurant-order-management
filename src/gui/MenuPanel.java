package gui;

import manager.MenuManager;
import model.MenuItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MenuPanel extends JPanel {
    private MenuManager menuManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtCategory, txtPrice, txtDescription, txtSearch;
    private JComboBox<String> cbCategoryFilter, cbSort;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private int selectedItemId = -1;

    public MenuPanel(MenuManager menuManager) {
        this.menuManager = menuManager;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildFilterBar();
        buildTable();
        buildFormPanel();
        loadTable(menuManager.getAll());
    }

    // ARAYÜZ

    private void buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBorder(BorderFactory.createTitledBorder("Arama ve Filtreleme"));

        bar.add(new JLabel("Ara:"));
        txtSearch = new JTextField(14);
        bar.add(txtSearch);

        bar.add(new JLabel("Kategori:"));
        cbCategoryFilter = new JComboBox<>();
        cbCategoryFilter.addItem("Tumü");
        bar.add(cbCategoryFilter);

        bar.add(new JLabel("Sirala:"));
        cbSort = new JComboBox<>(new String[]{
            "Varsayilan", "Fiyat (Dusuk-Yuksek)", "Fiyat (Yuksek-Dusuk)", "Isme Gore (A-Z)"
        });
        bar.add(cbSort);

        JButton btnFilter = new JButton("Uygula");
        btnFilter.setBackground(new Color(60, 100, 180));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> applyFilterAndSort());
        bar.add(btnFilter);

        add(bar, BorderLayout.NORTH);
    }

    private void buildTable() {
        String[] columns = {"ID", "Urun Adi", "Kategori", "Fiyat (TL)", "Aciklama"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));


        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(3).setMaxWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Menu Listesi"));
        add(scroll, BorderLayout.CENTER);


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });
    }

    private void buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Urun Ekle / Duzenle"));
        form.setPreferredSize(new Dimension(240, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0; form.add(new JLabel("Urun Adi:"), gbc);
        gbc.gridy = 1; txtName = new JTextField(); form.add(txtName, gbc);
        gbc.gridy = 2; form.add(new JLabel("Kategori:"), gbc);
        gbc.gridy = 3; txtCategory = new JTextField(); form.add(txtCategory, gbc);
        gbc.gridy = 4; form.add(new JLabel("Fiyat (TL):"), gbc);
        gbc.gridy = 5; txtPrice = new JTextField(); form.add(txtPrice, gbc);
        gbc.gridy = 6; form.add(new JLabel("Aciklama:"), gbc);
        gbc.gridy = 7;
        txtDescription = new JTextField();
        form.add(txtDescription, gbc);

        gbc.gridy = 8;
        btnAdd = makeButton("Ekle", new Color(40, 150, 40), Color.WHITE);
        form.add(btnAdd, gbc);

        gbc.gridy = 9;
        btnUpdate = makeButton("Guncelle", new Color(60, 100, 200), Color.WHITE);
        form.add(btnUpdate, gbc);

        gbc.gridy = 10;
        btnDelete = makeButton("Sil", new Color(190, 40, 40), Color.WHITE);
        form.add(btnDelete, gbc);

        gbc.gridy = 11;
        btnClear = makeButton("Temizle", new Color(120, 120, 120), Color.WHITE);
        form.add(btnClear, gbc);

        add(form, BorderLayout.EAST);

        btnAdd.addActionListener(e -> addItem());
        btnUpdate.addActionListener(e -> updateItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnClear.addActionListener(e -> clearForm());
    }


    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedItemId = (int) tableModel.getValueAt(row, 0);
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        txtCategory.setText(tableModel.getValueAt(row, 2).toString());
        txtPrice.setText(tableModel.getValueAt(row, 3).toString());
        txtDescription.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void addItem() {
        try {
            String name = txtName.getText().trim();
            String cat  = txtCategory.getText().trim();
            if (name.isEmpty() || cat.isEmpty()) {
                showError("Urun adi ve kategori bos olamaz!");
                return;
            }
            double price = Double.parseDouble(txtPrice.getText().trim());
            if (price < 0) { showError("Fiyat negatif olamaz!"); return; }
            MenuItem item = new MenuItem(0, name, cat, price, txtDescription.getText().trim());
            menuManager.addMenuItem(item);
            refreshAll();
            clearForm();
            JOptionPane.showMessageDialog(this, "Urun basariyla eklendi.");
        } catch (NumberFormatException ex) {
            showError("Gecerli bir fiyat giriniz!");
        }
    }

    private void updateItem() {
        if (selectedItemId < 0) { showError("Lutfen bir urun seciniz!"); return; }
        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            MenuItem item = new MenuItem(
                    selectedItemId,
                    txtName.getText().trim(),
                    txtCategory.getText().trim(),
                    price,
                    txtDescription.getText().trim()
            );
            menuManager.updateMenuItem(item);
            refreshAll();
            clearForm();
        } catch (NumberFormatException ex) {
            showError("Gecerli bir fiyat giriniz!");
        }
    }

    private void deleteItem() {
        if (selectedItemId < 0) { showError("Lutfen bir urun seciniz!"); return; }
        int c = JOptionPane.showConfirmDialog(this, "Secilen urunu silmek istediginize emin misiniz?",
                "Silme Onayi", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            menuManager.deleteMenuItem(selectedItemId);
            refreshAll();
            clearForm();
        }
    }

    private void applyFilterAndSort() {
        String search  = txtSearch.getText().trim().toLowerCase();
        String selCat  = (String) cbCategoryFilter.getSelectedItem();
        int sortChoice = cbSort.getSelectedIndex();


        ArrayList<MenuItem> list;
        switch (sortChoice) {
            case 1: list = menuManager.sortByPrice(true);  break;
            case 2: list = menuManager.sortByPrice(false); break;
            case 3: list = menuManager.sortByName();       break;
            default: list = menuManager.getAll();
        }

        if (selCat != null && !"Tumu".equals(selCat) && !"Tumü".equals(selCat)) {
            ArrayList<MenuItem> filtered = new ArrayList<>();
            for (MenuItem m : list) {
                if (m.getCategory().equals(selCat)) filtered.add(m);
            }
            list = filtered;
        }

        if (!search.isEmpty()) {
            ArrayList<MenuItem> searched = new ArrayList<>();
            for (MenuItem m : list) {
                if (m.getName().toLowerCase().contains(search)
                        || m.getCategory().toLowerCase().contains(search)
                        || m.getDescription().toLowerCase().contains(search)) {
                    searched.add(m);
                }
            }
            list = searched;
        }

        loadTable(list);
    }

    private void clearForm() {
        selectedItemId = -1;
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        table.clearSelection();
    }

    private void loadTable(ArrayList<MenuItem> items) {
        tableModel.setRowCount(0);
        for (MenuItem m : items) {
            tableModel.addRow(new Object[]{
                m.getId(),
                m.getName(),
                m.getCategory(),
                String.format("%.2f", m.getPrice()),
                m.getDescription()
            });
        }
    }

    private void refreshAll() {
        loadTable(menuManager.getAll());

        cbCategoryFilter.removeAllItems();
        cbCategoryFilter.addItem("Tumu");
        for (String cat : menuManager.getCategories()) cbCategoryFilter.addItem(cat);
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Hata", JOptionPane.ERROR_MESSAGE);
    }
}
