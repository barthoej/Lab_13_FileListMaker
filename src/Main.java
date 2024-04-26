import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;

public class Main
{
    private static ArrayList<String> myList = new ArrayList<>();
    private static boolean needsToBeSaved = false;
    private static String currentFileName = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMenu();
            String choice = SafeInput.getRegExString(scanner, "Enter your choice: [AaDdVvOSsCcqQ]", "[AaDdVvOSsCcqQ]");
            switch (choice.toUpperCase()) {
                case "A":
                    addItem();
                    break;
                case "D":
                    deleteItem();
                    break;
                case "V":
                    viewList();
                    break;
                case "O":
                    openList(scanner);
                    break;
                case "S":
                    saveList();
                    break;
                case "C":
                    clearList();
                    break;
                case "Q":
                    if (confirmQuit()) {
                        if (needsToBeSaved) {
                            System.out.println("Unsaved changes! Please save or discard the list.");
                            break;
                        }
                        System.out.println("Exiting the program...");
                        scanner.close();
                        return;
                    }
                    break;
            }
        }
    }

    private static void displayMenu() {
        System.out.println("Menu:");
        System.out.println("A - Add an item to the list");
        System.out.println("D - Delete an item from the list");
        System.out.println("V - View the list");
        System.out.println("O - Open a list file from disk");
        System.out.println("S - Save the current list to disk");
        System.out.println("C - Clear the current list");
        System.out.println("Q - Quit the program");
    }

    private static void addItem() {
        System.out.print("Enter the item to add: ");
        String item = new Scanner(System.in).nextLine();
        myList.add(item);
        needsToBeSaved = true;
    }

    private static void deleteItem() {
        if (myList.isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }
        System.out.println("Current list:");
        printNumberedList();
        int index = SafeInput.getRangedInt(new Scanner(System.in), "Enter the number of the item to delete: ", 1, myList.size()) - 1;
        String deletedItem = myList.remove(index);
        System.out.println("Item \"" + deletedItem + "\" deleted successfully!");
        needsToBeSaved = true;
    }

    private static void viewList() {
        if (myList.isEmpty()) {
            System.out.println("The list is empty.");
        } else {
            System.out.println("Current list:");
            for (String item : myList) {
                System.out.println("- " + item);
            }
        }
    }

    private static void openList(Scanner scanner) {
        if (needsToBeSaved) {
            System.out.println("Unsaved changes! Please save or discard the list.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFileName = file.getName();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                myList.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    myList.add(line);
                }
                System.out.println("List loaded from file: " + currentFileName);
                needsToBeSaved = false;
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
    }

    private static void saveList() {
        if (myList.isEmpty()) {
            System.out.println("The list is empty. Nothing to save.");
            return;
        }
        if (currentFileName.isEmpty()) {
            currentFileName = SafeInput.getRegExString(new Scanner(System.in), "Enter the filename to save: ", "[\\w]+\\.txt");
        }
        try (PrintWriter writer = new PrintWriter(currentFileName)) {
            for (String item : myList) {
                writer.println(item);
            }
            System.out.println("List saved to file: " + currentFileName);
            needsToBeSaved = false;
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    private static void clearList() {
        myList.clear();
        System.out.println("List cleared.");
        needsToBeSaved = false;
    }

    private static void printNumberedList() {
        for (int i = 0; i < myList.size(); i++) {
            System.out.println((i + 1) + ". " + myList.get(i));
        }
    }

    private static boolean confirmQuit() {
        return SafeInput.getYNConfirm(new Scanner(System.in), "Are you sure you want to quit? (Y/N): ");
    }
}
