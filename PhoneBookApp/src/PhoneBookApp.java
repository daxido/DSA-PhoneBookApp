import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Contact Class
class Contact {
    private String name;
    private String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Phone Number: " + phoneNumber;
    }
}

// Binary Search Tree Node
class BSTNode {
    Contact contact;
    BSTNode left, right;

    public BSTNode(Contact contact) {
        this.contact = contact;
        left = right = null;
    }
}

// Binary Search Tree Phonebook
class BSTPhonebook {
    private BSTNode root;
    private List<String> searchHistory; // To store search history
    private List<Contact> usageStats; // To track contact usage

    public BSTPhonebook() {
        root = null;
        searchHistory = new ArrayList<>();
        usageStats = new ArrayList<>();
    }

    // Insert Contact
    public void insertContact(String name, String phoneNumber) {
        if (!phoneNumber.startsWith("81")) {
            System.out.println("Error: Phone number must start with '81'.");
            return;
        }

        String formattedPhoneNumber = "+264" + phoneNumber;
        Contact newContact = new Contact(name, formattedPhoneNumber);
        root = insertRecursive(root, newContact);
        System.out.println("Contact added: " + name + " (" + formattedPhoneNumber + ")");
    }

    private BSTNode insertRecursive(BSTNode root, Contact contact) {
        if (root == null) {
            root = new BSTNode(contact);
            return root;
        }

        if (contact.getName().compareToIgnoreCase(root.contact.getName()) < 0) {
            root.left = insertRecursive(root.left, contact);
        } else if (contact.getName().compareToIgnoreCase(root.contact.getName()) > 0) {
            root.right = insertRecursive(root.right, contact);
        }

        return root;
    }

    // Search Contact by Name (Exact and Partial)
    public List<Contact> searchContact(String name) {
        List<Contact> foundContacts = new ArrayList<>();
        searchHistory.add(name); // Add to search history
        searchRecursive(root, name, foundContacts);
        return foundContacts;
    }

    private void searchRecursive(BSTNode root, String name, List<Contact> foundContacts) {
        if (root != null) {
            // Check current contact for exact and partial matches
            if (root.contact.getName().equalsIgnoreCase(name) ||
                    root.contact.getName().toLowerCase().contains(name.toLowerCase())) {
                foundContacts.add(root.contact);
                usageStats.add(root.contact); // Track usage
            }

            // Search left and right
            searchRecursive(root.left, name, foundContacts);
            searchRecursive(root.right, name, foundContacts);
        }
    }

    // Search Contact by Phone Number
    public Contact searchByPhoneNumber(String phoneNumber) {
        return searchByPhoneNumberRecursive(root, phoneNumber);
    }

    private Contact searchByPhoneNumberRecursive(BSTNode root, String phoneNumber) {
        if (root == null) {
            return null;
        }

        if (root.contact.getPhoneNumber().equals(phoneNumber)) {
            usageStats.add(root.contact); // Track usage
            return root.contact;
        }

        Contact foundContact = searchByPhoneNumberRecursive(root.left, phoneNumber);
        if (foundContact != null) {
            return foundContact;
        }
        return searchByPhoneNumberRecursive(root.right, phoneNumber);
    }

    // Display All Contacts (in-order traversal)
    public void displayAllContacts() {
        if (root == null) {
            System.out.println("No contacts available.");
        } else {
            inOrderTraversal(root);
        }
    }

    private void inOrderTraversal(BSTNode root) {
        if (root != null) {
            inOrderTraversal(root.left);
            System.out.println(root.contact);
            inOrderTraversal(root.right);
        }
    }

    // Delete Contact
    public void deleteContact(String name) {
        root = deleteRecursive(root, name);
    }

    private BSTNode deleteRecursive(BSTNode root, String name) {
        if (root == null) {
            System.out.println("Contact not found.");
            return root;
        }

        if (name.compareToIgnoreCase(root.contact.getName()) < 0) {
            root.left = deleteRecursive(root.left, name);
        } else if (name.compareToIgnoreCase(root.contact.getName()) > 0) {
            root.right = deleteRecursive(root.right, name);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            root.contact = findMin(root.right);
            root.right = deleteRecursive(root.right, root.contact.getName());
        }

        return root;
    }

    private Contact findMin(BSTNode root) {
        while (root.left != null) {
            root = root.left;
        }
        return root.contact;
    }

    // Edit Contact
    public void editContact(String name, String newName, String newPhoneNumber) {
        if (!newPhoneNumber.startsWith("81")) {
            System.out.println("Error: Phone number must start with '81'.");
            return;
        }

        String formattedPhoneNumber = "+264" + newPhoneNumber;
        deleteContact(name);  // Remove old contact
        insertContact(newName, formattedPhoneNumber);  // Insert updated contact
        System.out.println("Contact updated: " + name);
    }

    // Export contacts to file (in-order traversal)
    public void exportContactsToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            exportRecursive(root, writer);
            System.out.println("Contacts exported to file " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Error exporting contacts to file.");
        }
    }

    private void exportRecursive(BSTNode root, PrintWriter writer) {
        if (root != null) {
            exportRecursive(root.left, writer);
            writer.println(root.contact.getName() + "," + root.contact.getPhoneNumber());
            exportRecursive(root.right, writer);
        }
    }

    // Import contacts from file
    public void importContactsFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 2) {
                    insertContact(parts[0], parts[1].replace("+264", ""));  // Remove "+264" during import
                }
            }
            System.out.println("Contacts imported from file " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Error importing contacts from file.");
        }
    }

    // Display Usage Stats
    public void displayUsageStats() {
        if (usageStats.isEmpty()) {
            System.out.println("No usage statistics available.");
            return;
        }
        System.out.println("Contact Usage Statistics:");
        for (Contact contact : usageStats) {
            System.out.println(contact);
        }
    }

    // Display Search History
    public void displaySearchHistory() {
        if (searchHistory.isEmpty()) {
            System.out.println("No search history available.");
            return;
        }
        System.out.println("Search History:");
        for (String search : searchHistory) {
            System.out.println(search);
        }
    }

    // Sort Contacts
    public List<Contact> sortContacts(Comparator<Contact> comparator) {
        List<Contact> sortedContacts = new ArrayList<>();
        inOrderTraversalToList(root, sortedContacts);
        Collections.sort(sortedContacts, comparator);
        return sortedContacts;
    }

    private void inOrderTraversalToList(BSTNode root, List<Contact> list) {
        if (root != null) {
            inOrderTraversalToList(root.left, list);
            list.add(root.contact);
            inOrderTraversalToList(root.right, list);
        }
    }
}

// Main Program
public class PhoneBookApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BSTPhonebook phonebook = new BSTPhonebook();

        while (true) {
            System.out.println("\nPhonebook Menu:");
            System.out.println("1. Insert Contact");
            System.out.println("2. Search Contact");
            System.out.println("3. Display All Contacts");
            System.out.println("4. Delete Contact");
            System.out.println("5. Edit Contact");
            System.out.println("6. Search by Phone Number");
            System.out.println("7. Display Usage Stats");
            System.out.println("8. Display Search History");
            System.out.println("9. Sort Contacts by Name");
            System.out.println("10.Sort Contacts by Phone Number");
            System.out.println("11. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter phone number (81xxxxxxx): ");
                    String phoneNumber = scanner.nextLine();
                    phonebook.insertContact(name, phoneNumber);
                    break;

                case 2:
                    System.out.print("Enter name to search: ");
                    String searchName = scanner.nextLine();
                    List<Contact> foundContacts = phonebook.searchContact(searchName);
                    if (foundContacts.isEmpty()) {
                        System.out.println("No contacts found.");
                    } else {
                        System.out.println("Found Contacts:");
                        for (Contact contact : foundContacts) {
                            System.out.println(contact);
                        }
                    }
                    break;

                case 3:
                    phonebook.displayAllContacts();
                    break;

                case 4:
                    System.out.print("Enter name to delete: ");
                    String deleteName = scanner.nextLine();
                    phonebook.deleteContact(deleteName);
                    break;

                case 5:
                    System.out.print("Enter name of contact to edit: ");
                    String oldName = scanner.nextLine();
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new phone number (81xxxxxxx): ");
                    String newPhoneNumber = scanner.nextLine();
                    phonebook.editContact(oldName, newName, newPhoneNumber);
                    break;

                case 6:
                    System.out.print("Enter phone number to search: ");
                    String searchPhoneNumber = scanner.nextLine();
                    Contact foundContact = phonebook.searchByPhoneNumber(searchPhoneNumber);
                    if (foundContact != null) {
                        System.out.println("Found Contact: " + foundContact);
                    } else {
                        System.out.println("No contact found with that phone number.");
                    }
                    break;

                case 7:
                    phonebook.displayUsageStats();
                    break;

                case 8:
                    phonebook.displaySearchHistory();
                    break;

                case 9:
                    List<Contact> sortedByName = phonebook.sortContacts(Comparator.comparing(Contact::getName));
                    System.out.println("Contacts Sorted by Name:");
                    for (Contact contact : sortedByName) {
                        System.out.println(contact);
                    }
                    break;

                case 10:
                    List<Contact> sortedByPhoneNumber = phonebook.sortContacts(Comparator.comparing(Contact::getPhoneNumber));
                    System.out.println("Contacts Sorted by Phone Number:");
                    for (Contact contact : sortedByPhoneNumber) {
                        System.out.println(contact);
                    }
                    break;

                case 11:
                    System.out.println("Exiting the phonebook application.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}
