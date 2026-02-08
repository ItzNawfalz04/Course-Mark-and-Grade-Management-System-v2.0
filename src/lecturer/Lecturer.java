package Lecturer;

import Main.Main;
import Main.User;
import java.util.Scanner;

public class Lecturer extends User {
    private String department;
    
    public Lecturer(String name, String workId, String username, String password) {
        super(name, workId, username, password, "Lecturer");
    }
    
    public String getWorkId() {
        return getId(); // Lecturer ID is work ID
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
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
            System.out.println(">> Lecturer Menu");
            System.out.println("---------------------------------------------------------------");
            System.out.println("\n[LECTURER INFORMATION]");
            System.out.println("Name       : " + name);
            System.out.println("Work ID    : " + id);
            System.out.println("User Type  : " + role);
            System.out.println("\n---------------------------------------------------------------\n");
            
            System.out.println("[1] View Assigned Courses");
            System.out.println("[2] View Students in a Course");
            System.out.println("[3] Update Student Marks");
            System.out.println("[4] Logout");
            System.out.println("\n---------------------------------------------------------------");
            System.out.print("Enter your choice (1-5): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    Main.clearScreen();
                    ViewAssignedCourse.view(getWorkId());
                    break;
                case "2":
                    Main.clearScreen();
                    ViewStudentsInCourse.view(getWorkId(), scanner);
                    break;
                case "3":
                    Main.clearScreen();
                    UpdateStudentsMarks.update(getWorkId(), scanner);
                    break;
                case "4":
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-4.");
            }
            
            if (loggedIn) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                Main.clearScreen();
            }
        }
    }
}