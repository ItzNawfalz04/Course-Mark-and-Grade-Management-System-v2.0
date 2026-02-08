package Student;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class RegisterCourse {
    private static final String COURSES_FILE = Paths.get("csv_database", "Courses.csv").toString();
    private static final String COURSE_MARKS_DIR = Paths.get("csv_database", "CourseMarks").toString();
    
    public static void register(String matricNo, Scanner scanner) {
        System.out.println("==============================================================================");
        System.out.println("                     COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                         --- FACULTY OF COMPUTING ---");
        System.out.println("                      UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("==============================================================================");
        System.out.println(">> Student Menu >> Register Course");
        System.out.println("------------------------------------------------------------------------------");
        
        try {
            // Read all available courses
            List<String[]> allCourses = readAvailableCourses();
            
            if (allCourses.isEmpty()) {
                System.out.println("No courses available in the system.");
                return;
            }
            
            // Separate registered and available courses
            List<String[]> registeredCourses = new ArrayList<>();
            List<String[]> availableCourses = new ArrayList<>();
            
            for (String[] course : allCourses) {
                String courseCode = course[1];
                if (isAlreadyRegistered(matricNo, courseCode)) {
                    registeredCourses.add(course);
                } else {
                    availableCourses.add(course);
                }
            }
            
            // Display registered courses
            System.out.println("\nYour Registered Courses:");
            System.out.println("------------------------------------------------------------------------------");
            
            if (registeredCourses.isEmpty()) {
                System.out.println("You have not registered for any courses yet.");
            } else {
                System.out.printf("%-3s %-40s %-15s %-10s%n", "No.", "Course Name", "Course Code", "Credit Hour");
                System.out.println("------------------------------------------------------------------------------");
                
                for (int i = 0; i < registeredCourses.size(); i++) {
                    String[] course = registeredCourses.get(i);
                    System.out.printf("%-3d %-40s %-15s %-10s%n", 
                        (i + 1), course[0], course[1], course[2]);
                }
                
                int totalCredits = calculateTotalCredits(registeredCourses);
                System.out.println("------------------------------------------------------------------------------");
                System.out.println("Total Registered Credit Hours: " + totalCredits);
                System.out.println("------------------------------------------------------------------------------");
            }
            
            // Display available courses
            System.out.println("\nAvailable Courses (Not Yet Registered):");
            System.out.println("------------------------------------------------------------------------------");
            
            if (availableCourses.isEmpty()) {
                System.out.println("You have already registered for all available courses.");
            } else {
                System.out.printf("%-3s %-40s %-15s %-10s%n", "No.", "Course Name", "Course Code", "Credit Hour");
                System.out.println("------------------------------------------------------------------------------");
                
                for (int i = 0; i < availableCourses.size(); i++) {
                    String[] course = availableCourses.get(i);
                    System.out.printf("%-3d %-40s %-15s %-10s%n", 
                        (i + 1), course[0], course[1], course[2]);
                }
                
                System.out.println("------------------------------------------------------------------------------");
                System.out.print("Enter course number to register (or 0 to cancel): ");
                
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    
                    if (choice == 0) {
                        System.out.println("\nRegister Course Operation cancelled.");
                        return;
                    }
                    
                    if (choice < 1 || choice > availableCourses.size()) {
                        System.out.println("\nInvalid choice! Please enter a number between 1 and " + availableCourses.size() + ".");
                        return;
                    }
                    
                    String[] selectedCourse = availableCourses.get(choice - 1);
                    String courseCode = selectedCourse[1];
                    String courseName = selectedCourse[0];
                    String creditHour = selectedCourse[2];
                    
                    // Show confirmation
                    System.out.println("\nCourse Registration Details:");
                    System.out.println("------------------------------------------------------------------------------");
                    System.out.println("Course Name  : " + courseName);
                    System.out.println("Course Code  : " + courseCode);
                    System.out.println("Credit Hour  : " + creditHour);
                    System.out.println("Student ID   : " + matricNo);
                    System.out.println("------------------------------------------------------------------------------");
                    System.out.print("Confirm course registration? (Y/N): ");
                    
                    String confirm = scanner.nextLine().trim().toUpperCase();
                    
                    if (confirm.equals("Y")) {
                        // Register the student
                        if (addStudentToCourse(matricNo, courseCode)) {
                            System.out.println("Successfully registered for " + courseCode + "!");
                            System.out.println("Initial marks (Coursework: 0, Final Exam: 0) have been set.");
                        } else {
                            System.out.println("Failed to register for " + courseCode + ".");
                        }
                    } else {
                        System.out.println("Registration cancelled.");
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("\nInvalid input! Please enter a number.");
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error: Could not read course information.");
            System.out.println("Error details: " + e.getMessage());
        }
    }
    
    private static List<String[]> readAvailableCourses() throws IOException {
        List<String[]> courses = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String courseName = values[0].trim();
                    String courseCode = values[1].trim();
                    String creditHour = values[2].trim();
                    courses.add(new String[]{courseName, courseCode, creditHour});
                }
            }
        }
        return courses;
    }
    
    private static boolean isAlreadyRegistered(String matricNo, String courseCode) throws IOException {
        String courseFile = Paths.get(COURSE_MARKS_DIR, courseCode + ".csv").toString();
        
        if (!Files.exists(Paths.get(courseFile))) {
            return false;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 1 && values[0].trim().equals(matricNo)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static int calculateTotalCredits(List<String[]> courses) {
        int total = 0;
        for (String[] course : courses) {
            try {
                total += Integer.parseInt(course[2]);
            } catch (NumberFormatException e) {
                // Skip if credit hour is not a valid number
            }
        }
        return total;
    }
    
    private static boolean addStudentToCourse(String matricNo, String courseCode) {
        String courseFile = Paths.get(COURSE_MARKS_DIR, courseCode + ".csv").toString();
        
        try {
            // Create directory if it doesn't exist
            Path directory = Paths.get(COURSE_MARKS_DIR);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // Create file if it doesn't exist
            if (!Files.exists(Paths.get(courseFile))) {
                Files.createFile(Paths.get(courseFile));
            }
            
            // Append student to the course file with initial marks 0,0
            try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(courseFile, true), StandardCharsets.UTF_8))) {
                writer.write(matricNo + ",0,0");
                writer.newLine();
            }
            return true;
            
        } catch (IOException e) {
            System.out.println("Error writing to course file: " + e.getMessage());
            return false;
        }
    }
}