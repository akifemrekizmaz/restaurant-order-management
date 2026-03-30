package manager;

import model.User;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private ArrayList<User> users;
    private static final String FILE_NAME = "users.txt";

    public UserManager() {
        this.users = new ArrayList<>();
        loadFromFile();
        if (users.isEmpty()) {
 
            users.add(new User("admin", "1234", "ADMIN"));
            users.add(new User("garson1", "1234", "GARSON"));
            saveToFile();
        }
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null; // Başarısız 
    }

    public void addUser(User user) {
        users.add(user);
        saveToFile();
    }

    public void deleteUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
        saveToFile();
    }

    public ArrayList<User> getAll() {
        return new ArrayList<>(users);
    }

    public boolean usernameExists(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return true;
        }
        return false;
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        for (User u : users) lines.add(u.toString());
        FileManager.writeLines(FILE_NAME, lines);
    }

    private void loadFromFile() {
        ArrayList<String> lines = FileManager.readLines(FILE_NAME);
        for (String line : lines) {
            try {
                users.add(User.fromString(line));
            } catch (Exception e) {
                System.err.println("Kullanici yukleme hatasi: " + line);
            }
        }
    }
}
