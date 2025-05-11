import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// Class made to read files
// Most of the code regarding BufferedReader I received help with online
public class ReadFile {
    public static void main(String[] args) {
        // Insert file address in the quotes
        String file = "file.txt";
        // Call the method to read the integers
        int[] integers = readIntegers(file);
        // Print the integers
        for (int num : integers) {
            System.out.print(num + " ");
        }
    }

    // Method to read integers from a file and return them as an array
    public static int[] readIntegers(String file) {
        ArrayList<Integer> integersList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    int num = Integer.parseInt(line.trim());
                    integersList.add(num);
                } catch (NumberFormatException e) {
                    // Handle the case where the line is not a valid integer
                    System.err.println("Invalid integer in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Convert ArrayList to int array
        int[] integersArray = new int[integersList.size()];
        for (int i = 0; i < integersList.size(); i++) {
            integersArray[i] = integersList.get(i);
        }
        return integersArray;
    }
}
