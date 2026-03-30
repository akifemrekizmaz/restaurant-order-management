package manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static void writeLines(String filename, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Dosya yazma hatasi [" + filename + "]: " + e.getMessage());
        }
    }

    public static ArrayList<String> readLines(String filename) {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            return lines; // Dosya yoksa boş liste
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okuma hatasi [" + filename + "]: " + e.getMessage());
        }
        return lines;
    }
}
