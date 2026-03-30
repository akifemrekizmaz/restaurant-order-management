package model;

public class MenuItem {
    private int id;
    private String name;
    private String category;
    private double price;
    private String description;

    public MenuItem(int id, String name, String category, double price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {

        String safeDesc = description.replace(",", "|");
        String safeName = name.replace(",", "|");
        return id + "," + safeName + "," + category + "," + price + "," + safeDesc;
    }

    public static MenuItem fromString(String line) {
        String[] parts = line.split(",", 5);
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim().replace("|", ",");
        String category = parts[2].trim();
        double price = Double.parseDouble(parts[3].trim());
        String description = parts.length > 4 ? parts[4].trim().replace("|", ",") : "";
        return new MenuItem(id, name, category, price, description);
    }
}
