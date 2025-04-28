import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    public static List<String> ReadFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // public static void main(String[] args) {
    //     String fileName = "Actions1.txt";
    //     List<String> lines = readFile(fileName);
    //     for (String line : lines) {
    //         System.out.println(line);
    //     }
    // }
}