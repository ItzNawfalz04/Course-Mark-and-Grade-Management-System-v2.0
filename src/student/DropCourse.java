package Student;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class DropCourse {
    private static final String COURSES_FILE = Paths.get("csv_database", "Courses.csv").toString();
    private static final String COURSE_MARKS_DIR = Paths.get("csv_database", "CourseMarks").toString();
    
    public static void drop(String matricNo, Scanner scanner) {
        System.out.println("==============================================================================");
        System.out.println("                     COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                         --- FACULTY OF COMPUTING ---");
        System.out.println("                      UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("==============================================================================");
        System.out.println(">> Student Menu >> Drop Course");
        System.out.println("------------------------------------------------------------------------------");
        
        try {
            // Get registered courses for this student
            List<String[]> registeredCourses = getRegisteredCourses(matricNo);
            
            if (registeredCourses.isEmpty()) {
                System.out.println("\nYou are not registered for any courses.");
                System.out.println("Returning to Student Menu...");
                return;
            }
            
            // Display registered courses
            System.out.println("\nYour Registered Courses:");
            System.out.println("------------------------------------------------------------------------------");
            System.out.printf("%-3s %-40s %-15s %-10s%n", "No.", "Course Name", "Course Code", "Credit Hour");
            System.out.println("------------------------------------------------------------------------------");
            
            for (int i = 0; i < registeredCourses.size(); i++) {
                String[] course = registeredCourses.get(i);
                System.out.printf("%-3d %-40s %-15s %-10s%n", 
                    (i + 1), course[0], course[1], course[2]);
            }
            
            // Show total credit hours
            int totalCredits = calculateTotalCredits(registeredCourses);
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Total Registered Credit Hours: " + totalCredits);
            System.out.println("------------------------------------------------------------------------------");
            
            System.out.print("\nEnter course number to drop (or 0 to cancel): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                if (choice == 0) {
                    System.out.println("\nDrop Course operation cancelled.");
                    return;
                }
                
                if (choice < 1 || choice > registeredCourses.size()) {
                    System.out.println("\nInvalid choice! Please enter a number between 1 and " + registeredCourses.size() + ".");
                    return;
                }
                
                String[] selectedCourse = registeredCourses.get(choice - 1);
                String courseCode = selectedCourse[1];
                String courseName = selectedCourse[0];
                String creditHour = selectedCourse[2];
                
                // Get current marks for this course (for display in confirmation)
                String[] currentMarks = getCurrentMarks(matricNo, courseCode);
                String courseworkMark = "N/A";
                String finalExamMark = "N/A";
                
                if (currentMarks != null) {
                    courseworkMark = currentMarks[0];
                    finalExamMark = currentMarks[1];
                }
                
                // Show confirmation
                System.out.println("\nCourse Drop Details:");
                System.out.println("------------------------------------------------------------------------------");
                System.out.println("Course Name        : " + courseName);
                System.out.println("Course Code        : " + courseCode);
                System.out.println("Credit Hour        : " + creditHour);
                System.out.println("Student ID         : " + matricNo);
                System.out.println("Current Coursework : " + courseworkMark);
                System.out.println("Current Final Exam : " + finalExamMark);
                System.out.println("\nWARNING: Dropping this course will remove ALL your marks for this course.");
                System.out.println("         This action cannot be undone!");
                System.out.println("------------------------------------------------------------------------------");
                System.out.print("Are you sure you want to drop this course? (Y/N): ");
                
                String confirm = scanner.nextLine().trim().toUpperCase();
                
                if (confirm.equals("Y")) {
                    // Remove student from course
                    if (removeStudentFromCourse(matricNo, courseCode)) {
                        System.out.println("\nSuccessfully dropped " + courseCode + "!");
                        System.out.println("All your marks for this course have been removed.");
                    } else {
                        System.out.println("\nFailed to drop " + courseCode + ".");
                    }
                } else {
                    System.out.println("\nDrop operation cancelled.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input! Please enter a number.");
            }
            
        } catch (IOException e) {
            System.out.println("Error: Could not read course information.");
            System.out.println("Error details: " + e.getMessage());
        }
    }
    
    private static List<String[]> getRegisteredCourses(String matricNo) throws IOException {
        List<String[]> registeredCourses = new ArrayList<>();
        
        // First read all available courses
        List<String[]> allCourses = readAvailableCourses();
        
        // Check which courses the student is registered in
        for (String[] course : allCourses) {
            String courseCode = course[1];
            String courseFile = Paths.get(COURSE_MARKS_DIR, courseCode + ".csv").toString();
            
            if (Files.exists(Paths.get(courseFile))) {
                try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.replace("\uFEFF", "").trim();
                        if (line.isEmpty()) continue;
                        
                        String[] values = line.split(",");
                        if (values.length >= 1 && values[0].trim().equals(matricNo)) {
                            registeredCourses.add(course);
                            break;
                        }
                    }
                }
            }
        }
        return registeredCourses;
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
    
    private static String[] getCurrentMarks(String matricNo, String courseCode) throws IOException {
        String courseFile = Paths.get(COURSE_MARKS_DIR, courseCode + ".csv").toString();
        
        if (!Files.exists(Paths.get(courseFile))) {
            return null;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 3 && values[0].trim().equals(matricNo)) {
                    return new String[]{values[1].trim(), values[2].trim()};
                }
            }
        }
        return null;
    }
    
    private static boolean removeStudentFromCourse(String matricNo, String courseCode) {
        String courseFile = Paths.get(COURSE_MARKS_DIR, courseCode + ".csv").toString();
        
        if (!Files.exists(Paths.get(courseFile))) {
            System.out.println("Course file not found: " + courseFile);
            return false;
        }
        
        try {
            // Read all lines except the one with the student's matric number
            List<String> lines = new ArrayList<>();
            boolean found = false;
            
            try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.replace("\uFEFF", "").trim();
                    if (line.isEmpty()) continue;
                    
                    String[] values = line.split(",");
                    if (values.length >= 1 && values[0].trim().equals(matricNo)) {
                        found = true;
                        continue; // Skip this line (remove student)
                    }
                    lines.add(line);
                }
            }
            
            if (!found) {
                System.out.println("Student not found in course " + courseCode);
                return false;
            }
            
            // Write back all lines except the removed one
            try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(courseFile), StandardCharsets.UTF_8))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            return true;
            
        } catch (IOException e) {
            System.out.println("Error updating course file: " + e.getMessage());
            return false;
        }
    }
}
