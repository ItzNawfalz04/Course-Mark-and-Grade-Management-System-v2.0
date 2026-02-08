package Main;

import java.util.Scanner;

public class Main {  
    public static void main(String[] args) {
        clearScreen();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    clearScreen();
                    login(scanner);
                    break;
                case "2":
                    System.out.println("\nThank you for using the system. Goodbye!\n");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid choice! Please enter 1 or 2.");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    clearScreen();
            }
        }
        scanner.close();
    }
    
    private static void displayMainMenu() {
        System.out.println("===============================================================");
        System.out.println("             COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                 --- FACULTY OF COMPUTING ---");
        System.out.println("              UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("===============================================================");
        System.out.println(">> Main Menu");
        System.out.println("---------------------------------------------------------------\n");
        System.out.println("[1] User Login");
        System.out.println("[2] Exit Program");
        System.out.println("\n---------------------------------------------------------------");
        System.out.print("Enter your choice (1-2): ");
    }
    
    private static void login(Scanner scanner) {
        System.out.println("===============================================================");
        System.out.println("             COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                 --- FACULTY OF COMPUTING ---");
        System.out.println("              UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("===============================================================");
        System.out.println(">> Main Menu >> User Login");
        System.out.println("---------------------------------------------------------------\n");
        System.out.print("Username : ");
        String username = scanner.nextLine();
        System.out.print("\nPassword : ");
        String password = scanner.nextLine();
        System.out.println("\n---------------------------------------------------------------");
        
        User user = User.login(username, password);
        
        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.getName() + "!");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            clearScreen();
            user.showMenu(scanner);
            clearScreen();
        } else {
            System.out.println("Error: Invalid username or password.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            clearScreen();
        }
    }
    
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}