package Student;

import java.util.Scanner;

import Main.Main;
import Main.User;

public class Student extends User {
    private double cgpa;
    
    public Student(String name, String matricNo, String username, String password) {
        super(name, matricNo, username, password, "Student");
    }
    
    public String getMatricNo() {
        return getId(); // Student ID is matric number
    }
    
    public double getCGPA() {
        return cgpa;
    }
    
    public void setCGPA(double cgpa) {
        this.cgpa = cgpa;
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
            System.out.println(">> Student Menu");
            System.out.println("---------------------------------------------------------------");
            System.out.println("\n[STUDENT INFORMATION]");
            System.out.println("Name        : " + name);
            System.out.println("Matric No.  : " + id);
            System.out.println("User Type   : " + role);
            System.out.println("\n---------------------------------------------------------------\n");
            
            System.out.println("[1] Register Course");
            System.out.println("[2] Drop Course");
            System.out.println("[3] View Registered Courses");
            System.out.println("[4] View Grades & CGPA");
            System.out.println("[5] Logout");
            System.out.println("\n---------------------------------------------------------------");
            System.out.print("Enter your choice (1-5): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    Main.clearScreen();
                    RegisterCourse.register(getMatricNo(), scanner);
                    break;
                case "2":
                    Main.clearScreen();
                    DropCourse.drop(getMatricNo(), scanner);
                    break;
                case "3":
                    Main.clearScreen();
                    ViewCourseRegistration.view(getMatricNo());
                    break;
                case "4":
                    Main.clearScreen();
                    ViewGradeCGPA.view(getMatricNo());
                    break;
                case "5":
                    Main.clearScreen();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-5.");
            }
            
            if (loggedIn) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                Main.clearScreen();
            }
        }
    }
}