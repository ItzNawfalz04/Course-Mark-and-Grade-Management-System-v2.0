package Admin;

import Main.Main;
import Main.User;
import java.util.Scanner;

public class Admin extends User {
    
    public Admin(String name, String adminId, String username, String password) {
        super(name, adminId, username, password, "Administrator");
    }
    
    @Override
    public void showMenu(Scanner scanner) {
        boolean loggedIn = true;
        
        while (loggedIn) {
            System.out.println("===============================================================");
            System.out.println("             COURSE MARK & GRADE MANAGEMENT SYSTEM");
            System.out.println("                 --- FACULTY OF COMPUTING ---");
            System.out.println("              UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
            System.out.println("===============================================================");
            System.out.println(">> Admin Menu");
            System.out.println("---------------------------------------------------------------");
            System.out.println("\n[ADMIN INFORMATION]");
            System.out.println("Name       : " + name);
            System.out.println("Admin ID   : " + id);
            System.out.println("User Type  : " + role);
            System.out.println("\n---------------------------------------------------------------\n");
            
            System.out.println("[1] Manage Students");
            System.out.println("[2] Manage Lecturers");
            System.out.println("[3] Manage Courses");
            System.out.println("[4] Logout");
            System.out.println("\n---------------------------------------------------------------");
            System.out.print("Enter your choice (1-4): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    Main.clearScreen();
                    ManageStudents.showMenu(scanner);
                    break;
                case "2":
                    Main.clearScreen();
                    ManageLecturers.showMenu(scanner);
                    break;
                case "3":
                    Main.clearScreen();
                    ManageCourses.showMenu(scanner);
                    break;
                case "4":
                    System.out.println("Logging out... Goodbye!");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-4.");
            }
            
            if (loggedIn) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }
}