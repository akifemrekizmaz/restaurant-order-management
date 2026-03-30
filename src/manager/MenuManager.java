package manager;

import model.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {
    private ArrayList<MenuItem> menuItems;
    private static final String FILE_NAME = "menu.txt";
    private int nextId;

    public MenuManager() {
        this.menuItems = new ArrayList<>();
        this.nextId = 1;
        loadFromFile();

        // Ilk calistirmada ornek veri yukle
        if (menuItems.isEmpty()) {
            addDefaultItems();
        }
    }

    public void addMenuItem(MenuItem item) {
        item.setId(nextId++);
        menuItems.add(item);
        saveToFile();
    }

    public void updateMenuItem(MenuItem updated) {
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId() == updated.getId()) {
                menuItems.set(i, updated);
                break;
            }
        }
        saveToFile();
    }

    public void deleteMenuItem(int id) {
        menuItems.removeIf(item -> item.getId() == id);
        saveToFile();
    }

    public MenuItem getById(int id) {
        for (MenuItem item : menuItems) {
            if (item.getId() == id) return item;
        }
        return null;
    }

    public ArrayList<MenuItem> getAll() {
        return new ArrayList<>(menuItems); 
    }
    
    public ArrayList<MenuItem> getByCategory(String category) {
        ArrayList<MenuItem> result = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<MenuItem> search(String keyword) {
        ArrayList<MenuItem> result = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (MenuItem item : menuItems) {
            if (item.getName().toLowerCase().contains(kw)
                    || item.getCategory().toLowerCase().contains(kw)
                    || item.getDescription().toLowerCase().contains(kw)) {
                result.add(item);
            }
        }
        return result;
    }

    // sıralama algortimaları
    public ArrayList<MenuItem> sortByPrice(boolean ascending) {
        ArrayList<MenuItem> sorted = new ArrayList<>(menuItems);
        int n = sorted.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                double a = sorted.get(j).getPrice();
                double b = sorted.get(j + 1).getPrice();
                boolean shouldSwap = ascending ? a > b : a < b;
                if (shouldSwap) {
                    MenuItem temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                }
            }
        }
        return sorted;
    }

    public ArrayList<MenuItem> sortByName() {
        ArrayList<MenuItem> sorted = new ArrayList<>(menuItems);
        int n = sorted.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (sorted.get(j).getName()
                        .compareToIgnoreCase(sorted.get(minIdx).getName()) < 0) {
                    minIdx = j;
                }
            }
            MenuItem temp = sorted.get(minIdx);
            sorted.set(minIdx, sorted.get(i));
            sorted.set(i, temp);
        }
        return sorted;
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> cats = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (!cats.contains(item.getCategory())) {
                cats.add(item.getCategory());
            }
        }
        return cats;
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        for (MenuItem item : menuItems) {
            lines.add(item.toString());
        }
        FileManager.writeLines(FILE_NAME, lines);
    }

    private void loadFromFile() {
        ArrayList<String> lines = FileManager.readLines(FILE_NAME);
        for (String line : lines) {
            try {
                MenuItem item = MenuItem.fromString(line);
                menuItems.add(item);
                if (item.getId() >= nextId) {
                    nextId = item.getId() + 1;
                }
            } catch (Exception e) {
                System.err.println("Gecersiz menu satiri atlandi: " + line);
            }
        }
    }

    private void addDefaultItems() {
        // Örnek menüler
        addMenuItem(new MenuItem(0, "Adana Kebap", "Ana Yemek", 120.0, "Acili kiyma kebabi"));
        addMenuItem(new MenuItem(0, "Lahmacun", "Ana Yemek", 45.0, "Ince hamur + kiyma"));
        addMenuItem(new MenuItem(0, "Mercimek Corbasi", "Corba", 35.0, "Klasik Turk corbasi"));
        addMenuItem(new MenuItem(0, "Ayran", "Icecek", 20.0, "Soguk yogurt icecegi"));
        addMenuItem(new MenuItem(0, "Kola", "Icecek", 25.0, "330ml"));
        addMenuItem(new MenuItem(0, "Sutlac", "Tatlı", 40.0, "Firinda sutlac"));
        addMenuItem(new MenuItem(0, "Baklava", "Tatlı", 55.0, "Antep fistikli"));
        addMenuItem(new MenuItem(0, "Pide", "Ana Yemek", 65.0, "Karisik pide"));
    }
}
