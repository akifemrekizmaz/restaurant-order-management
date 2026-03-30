package manager;

import model.MenuItem;
import model.Order;
import model.OrderItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {
    private ArrayList<Order> orders;
    private static final String FILE_NAME = "orders.txt";
    private int nextId;
    private MenuManager menuManager;

    public OrderManager(MenuManager menuManager) {
        this.menuManager = menuManager;
        this.orders = new ArrayList<>();
        this.nextId = 1;
        loadFromFile();
    }

    public Order createOrder(int tableNumber) {
        Order order = new Order(nextId++, tableNumber);
        orders.add(order);
        saveToFile();
        return order;
    }

    public void saveOrder(Order order) {
        saveToFile();
    }

    public void deleteOrder(int id) {
        orders.removeIf(o -> o.getId() == id);
        saveToFile();
    }

    public Order getById(int id) {
        for (Order o : orders) {
            if (o.getId() == id) return o;
        }
        return null;
    }

    public ArrayList<Order> getAll() {
        return new ArrayList<>(orders);
    }
    
    public ArrayList<Order> getByStatus(String status) {
        ArrayList<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus().equals(status)) result.add(o);
        }
        return result;
    }

    public ArrayList<Order> getByTable(int tableNumber) {
        ArrayList<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getTableNumber() == tableNumber) result.add(o);
        }
        return result;
    }

    public ArrayList<Order> sortByTotal(boolean ascending) {
        ArrayList<Order> sorted = new ArrayList<>(orders);
        for (int i = 1; i < sorted.size(); i++) {
            Order key = sorted.get(i);
            int j = i - 1;
            while (j >= 0) {
                double cmp = sorted.get(j).calculateTotal() - key.calculateTotal();
                boolean condition = ascending ? cmp > 0 : cmp < 0;
                if (condition) {
                    sorted.set(j + 1, sorted.get(j));
                    j--;
                } else {
                    break;
                }
            }
            sorted.set(j + 1, key);
        }
        return sorted;
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Order o : orders) {
            if (o.getStatus().equals("ODENDI")) {
                total += o.calculateTotal();
            }
        }
        return total;
    }

    public int getTotalOrderCount() {
        return orders.size();
    }

    public HashMap<String, Integer> getItemOrderCounts() {
        HashMap<String, Integer> counts = new HashMap<>();
        for (Order o : orders) {
            for (OrderItem item : o.getItems()) {
                String name = item.getMenuItem().getName();
                counts.put(name, counts.getOrDefault(name, 0) + item.getQuantity());
            }
        }
        return counts;
    }


    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        for (Order o : orders) {
            String note = o.getCustomerNote().replace("|", " ");
            lines.add("ORDER|" + o.getId() + "|" + o.getTableNumber() + "|"
                    + o.getStatus() + "|" + o.getTimestamp() + "|" + note);
            for (OrderItem item : o.getItems()) {
                lines.add("ITEM|" + item.getMenuItem().getId() + "|" + item.getQuantity());
            }
        }
        FileManager.writeLines(FILE_NAME, lines);
    }

    private void loadFromFile() {
        ArrayList<String> lines = FileManager.readLines(FILE_NAME);
        Order currentOrder = null;

        for (String line : lines) {
            try {
                if (line.startsWith("ORDER|")) {
                    String[] p = line.split("\\|", 6);
                    int id = Integer.parseInt(p[1]);
                    int table = Integer.parseInt(p[2]);
                    currentOrder = new Order(id, table);
                    currentOrder.setStatus(p[3]);
                    currentOrder.setTimestamp(p[4]);
                    currentOrder.setCustomerNote(p.length > 5 ? p[5] : "");
                    orders.add(currentOrder);
                    if (id >= nextId) nextId = id + 1;

                } else if (line.startsWith("ITEM|") && currentOrder != null) {
                    String[] p = line.split("\\|");
                    int menuId = Integer.parseInt(p[1]);
                    int qty = Integer.parseInt(p[2]);
                    MenuItem mi = menuManager.getById(menuId);
                    if (mi != null) {
                        currentOrder.getItems().add(new OrderItem(mi, qty));
                    }
                }
            } catch (Exception e) {
                System.err.println("Siparis yukleme hatasi, satir atlandi: " + line);
            }
        }
    }
}
