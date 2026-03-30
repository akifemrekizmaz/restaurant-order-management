package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Order {
    private int id;
    private int tableNumber;
    private ArrayList<OrderItem> items;
    private String status;           
    private String timestamp;
    private String customerNote;

    public Order(int id, int tableNumber) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.status = "BEKLIYOR";
        this.customerNote = "";
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public void addItem(OrderItem newItem) {
        for (OrderItem existing : items) {
            if (existing.getMenuItem().getId() == newItem.getMenuItem().getId()) {
                existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    public void removeItem(int menuItemId) {
        items.removeIf(item -> item.getMenuItem().getId() == menuItemId);
    }

    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }

    public ArrayList<OrderItem> getItems() { return items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }
}
