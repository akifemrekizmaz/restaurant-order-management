package gui;

import manager.MenuManager;
import manager.OrderManager;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// sipariş oluşturma ve yönetme sistemi

public class OrderPanel extends JPanel {
    private OrderManager orderManager;
    private MenuManager menuManager;
    private User currentUser;

    private JTable ordersTable;
    private DefaultTableModel ordersModel;

    private JTable itemsTable;
    private DefaultTableModel itemsModel;

    private JTextField txtTableNo, txtNote;
    private JComboBox<String> cbMenuItem, cbStatusFilter, cbStatus;
    private JSpinner spnQty;
    private JLabel lblTotal;
    private Order selectedOrder = null;

    public OrderPanel(OrderManager orderManager, MenuManager menuManager, User currentUser) {
        this.orderManager = orderManager;
        this.menuManager = menuManager;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buildToolbar();
        buildMainArea();
        refreshOrderList();
    }


    private void buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        bar.setBorder(BorderFactory.createEtchedBorder());

        bar.add(new JLabel("Masa No:"));
        txtTableNo = new JTextField(5);
        bar.add(txtTableNo);

        JButton btnNew = makeButton("Yeni Siparis", new Color(34, 139, 34), Color.WHITE);
        btnNew.addActionListener(e -> createNewOrder());
        bar.add(btnNew);

        bar.add(new JSeparator(SwingConstants.VERTICAL));
        bar.add(new JLabel("Durum Filtresi:"));
        cbStatusFilter = new JComboBox<>(new String[]{
            "Tumu", "BEKLIYOR", "HAZIRLANIYOR", "SERVIS_EDILDI", "ODENDI"
        });
        cbStatusFilter.addActionListener(e -> refreshOrderList());
        bar.add(cbStatusFilter);

        JButton btnRefresh = makeButton("Yenile", new Color(80, 80, 80), Color.WHITE);
        btnRefresh.addActionListener(e -> refreshOrderList());
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);
    }

    private void buildMainArea() {
       
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(420);
        split.setResizeWeight(0.4);

   
        String[] orderCols = {"ID", "Masa", "Durum", "Tarih", "Toplam"};
        ordersModel = new DefaultTableModel(orderCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setRowHeight(26);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 13));
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        ordersTable.getColumnModel().getColumn(0).setMaxWidth(40);

        ordersTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    String status = (String) t.getModel().getValueAt(row, 2);
                    switch (status) {
                        case "BEKLIYOR":      c.setBackground(new Color(255, 253, 220)); break;
                        case "HAZIRLANIYOR":  c.setBackground(new Color(220, 235, 255)); break;
                        case "SERVIS_EDILDI": c.setBackground(new Color(220, 255, 220)); break;
                        case "ODENDI":        c.setBackground(new Color(230, 230, 230)); break;
                        default:              c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane leftScroll = new JScrollPane(ordersTable);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Siparisler"));
        split.setLeftComponent(leftScroll);

        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onOrderSelected();
        });

        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));

        String[] itemCols = {"Urun Adi", "Adet", "Birim Fiyat", "Toplam"};
        itemsModel = new DefaultTableModel(itemCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        itemsTable = new JTable(itemsModel);
        itemsTable.setRowHeight(24);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setBorder(BorderFactory.createTitledBorder("Siparis Kalemleri"));
        rightPanel.add(itemsScroll, BorderLayout.CENTER);

        rightPanel.add(buildActionPanel(), BorderLayout.SOUTH);
        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Islemler"));

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        addRow.add(new JLabel("Urun:"));
        cbMenuItem = new JComboBox<>();
        reloadMenuCombo();
        cbMenuItem.setPreferredSize(new Dimension(200, 26));
        addRow.add(cbMenuItem);
        addRow.add(new JLabel("Adet:"));
        spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        addRow.add(spnQty);
        JButton btnAddItem = makeButton("Ekle", new Color(50, 120, 200), Color.WHITE);
        btnAddItem.addActionListener(e -> addItemToOrder());
        addRow.add(btnAddItem);
        JButton btnRemItem = makeButton("Cikar", new Color(200, 80, 30), Color.WHITE);
        btnRemItem.addActionListener(e -> removeItemFromOrder());
        addRow.add(btnRemItem);
        panel.add(addRow);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        statusRow.add(new JLabel("Not:"));
        txtNote = new JTextField(10);
        statusRow.add(txtNote);
        statusRow.add(new JLabel("Durum:"));
        cbStatus = new JComboBox<>(new String[]{"BEKLIYOR","HAZIRLANIYOR","SERVIS_EDILDI","ODENDI"});
        statusRow.add(cbStatus);
        JButton btnSave = makeButton("Kaydet", new Color(34, 139, 34), Color.WHITE);
        btnSave.addActionListener(e -> saveOrderChanges());
        statusRow.add(btnSave);
        JButton btnDel = makeButton("Sil", new Color(180, 30, 30), Color.WHITE);
        btnDel.addActionListener(e -> deleteSelectedOrder());
        statusRow.add(btnDel);
        panel.add(statusRow);

        lblTotal = new JLabel("  Toplam: 0.00 TL");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(200, 100, 0));
        panel.add(lblTotal);

        return panel;
    }

    private void createNewOrder() {
        String t = txtTableNo.getText().trim();
        if (t.isEmpty()) { showError("Masa numarasi giriniz!"); return; }
        try {
            int tableNo = Integer.parseInt(t);
            if (tableNo <= 0) { showError("Masa numarasi pozitif olmali!"); return; }
            Order o = orderManager.createOrder(tableNo);
            refreshOrderList();
            selectOrderById(o.getId());
        } catch (NumberFormatException ex) {
            showError("Gecerli bir masa numarasi giriniz!");
        }
    }

    private void onOrderSelected() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) { selectedOrder = null; return; }
        int id = (int) ordersModel.getValueAt(row, 0);
        selectedOrder = orderManager.getById(id);
        if (selectedOrder == null) return;
        refreshItemsTable();
        cbStatus.setSelectedItem(selectedOrder.getStatus());
        txtNote.setText(selectedOrder.getCustomerNote());
    }

    private void addItemToOrder() {
        if (selectedOrder == null) { showError("Lutfen bir siparis seciniz!"); return; }
        int idx = cbMenuItem.getSelectedIndex();
        if (idx < 0) return;
        ArrayList<MenuItem> all = menuManager.getAll();
        if (idx >= all.size()) return;
        MenuItem mi = all.get(idx);
        int qty = (int) spnQty.getValue();
        selectedOrder.addItem(new OrderItem(mi, qty));
        orderManager.saveOrder(selectedOrder);
        refreshItemsTable();
        updateTotalInList();
    }

    private void removeItemFromOrder() {
        if (selectedOrder == null) return;
        int row = itemsTable.getSelectedRow();
        if (row < 0) { showError("Kalemi secin!"); return; }
        String itemName = (String) itemsModel.getValueAt(row, 0);
        for (OrderItem oi : new ArrayList<>(selectedOrder.getItems())) {
            if (oi.getMenuItem().getName().equals(itemName)) {
                selectedOrder.removeItem(oi.getMenuItem().getId());
                break;
            }
        }
        orderManager.saveOrder(selectedOrder);
        refreshItemsTable();
        updateTotalInList();
    }

    private void saveOrderChanges() {
        if (selectedOrder == null) { showError("Lutfen bir siparis seciniz!"); return; }
        selectedOrder.setStatus((String) cbStatus.getSelectedItem());
        selectedOrder.setCustomerNote(txtNote.getText().trim());
        orderManager.saveOrder(selectedOrder);
        refreshOrderList();
        selectOrderById(selectedOrder.getId());
        JOptionPane.showMessageDialog(this, "Siparis guncellendi.");
    }

    private void deleteSelectedOrder() {
        if (selectedOrder == null) return;
        int c = JOptionPane.showConfirmDialog(this,
                "Siparis #" + selectedOrder.getId() + " silinsin mi?",
                "Silme Onayi", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            orderManager.deleteOrder(selectedOrder.getId());
            selectedOrder = null;
            itemsModel.setRowCount(0);
            lblTotal.setText("  Toplam: 0.00 TL");
            refreshOrderList();
        }
    }


    private void refreshOrderList() {
        int prevId = selectedOrder != null ? selectedOrder.getId() : -1;
        String filter = (String) cbStatusFilter.getSelectedItem();
        ArrayList<Order> list;
        if (filter != null && !"Tumu".equals(filter)) {
            list = orderManager.getByStatus(filter);
        } else {
            list = orderManager.getAll();
        }
        ordersModel.setRowCount(0);
        for (Order o : list) {
            ordersModel.addRow(new Object[]{
                o.getId(),
                "Masa " + o.getTableNumber(),
                o.getStatus(),
                o.getTimestamp(),
                String.format("%.2f TL", o.calculateTotal())
            });
        }
        if (prevId > 0) selectOrderById(prevId);
    }

    private void refreshItemsTable() {
        itemsModel.setRowCount(0);
        if (selectedOrder == null) return;
        for (OrderItem oi : selectedOrder.getItems()) {
            itemsModel.addRow(new Object[]{
                oi.getMenuItem().getName(),
                oi.getQuantity(),
                String.format("%.2f TL", oi.getMenuItem().getPrice()),
                String.format("%.2f TL", oi.getSubtotal())
            });
        }
        lblTotal.setText(String.format("  Toplam: %.2f TL", selectedOrder.calculateTotal()));
    }

    private void updateTotalInList() {
        if (selectedOrder == null) return;
        for (int i = 0; i < ordersModel.getRowCount(); i++) {
            if ((int) ordersModel.getValueAt(i, 0) == selectedOrder.getId()) {
                ordersModel.setValueAt(
                    String.format("%.2f TL", selectedOrder.calculateTotal()), i, 4);
                break;
            }
        }
    }

    private void selectOrderById(int id) {
        for (int i = 0; i < ordersModel.getRowCount(); i++) {
            if ((int) ordersModel.getValueAt(i, 0) == id) {
                ordersTable.setRowSelectionInterval(i, i);
                ordersTable.scrollRectToVisible(ordersTable.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private void reloadMenuCombo() {
        cbMenuItem.removeAllItems();
        for (MenuItem mi : menuManager.getAll()) {
            cbMenuItem.addItem(mi.getName() + " (" + String.format("%.2f", mi.getPrice()) + " TL)");
        }
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Hata", JOptionPane.ERROR_MESSAGE);
    }
}
