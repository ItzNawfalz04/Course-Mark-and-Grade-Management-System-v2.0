package Student;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ViewCourseRegistration {
    private static final String COURSES_FILE = Paths.get("csv_database", "Courses.csv").toString();
    private static final String COURSE_MARKS_DIR = Paths.get("csv_database", "CourseMarks").toString();
    private static final String STUDENTS_FILE = Paths.get("csv_database", "Students.csv").toString();
    
    public static void view(String matricNo) {
        System.out.println("==============================================================================");
        System.out.println("                     COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                         --- FACULTY OF COMPUTING ---");
        System.out.println("                      UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("==============================================================================");
        System.out.println(">> Student Menu >> View Course Registration");
        System.out.println("------------------------------------------------------------------------------");
        
        try {
            // Get student information
            String studentName = getStudentName(matricNo);
            
            if (studentName == null) {
                System.out.println("Error: Student information not found.");
                return;
            }
            
            // Display student information
            System.out.println("\n[STUDENT INFORMATION]");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Name           : " + studentName);
            System.out.println("Matric Number  : " + matricNo);
            System.out.println("------------------------------------------------------------------------------");
            
            // Get registered courses for this student
            List<String[]> registeredCourses = getRegisteredCourses(matricNo);
            
            if (registeredCourses.isEmpty()) {
                System.out.println("\nYou are not registered for any courses yet.");
                return;
            }
            
            // Display registered courses
            System.out.println("\n[REGISTERED COURSES]");
            System.out.println("------------------------------------------------------------------------------");
            System.out.printf("%-3s %-40s %-15s %-10s%n", "No.", "Course Name", "Course Code", "Credit Hour");
            System.out.println("------------------------------------------------------------------------------");
            
            int totalCredits = 0;
            int courseCount = 1;
            
            for (String[] course : registeredCourses) {
                String courseName = course[0];
                String courseCode = course[1];
                String creditHour = course[2];
                
                System.out.printf("%-3d %-40s %-15s %-10s%n", 
                    courseCount, courseName, courseCode, creditHour);
                
                try {
                    totalCredits += Integer.parseInt(creditHour);
                } catch (NumberFormatException e) {
                    // Skip if credit hour is not a valid number
                }
                
                courseCount++;
            }
            
            // Show summary
            System.out.println("------------------------------------------------------------------------------");
            System.out.printf("Total Number of Courses : %d%n", registeredCourses.size());
            System.out.printf("Total Credit Hours      : %d%n", totalCredits);
            System.out.println("------------------------------------------------------------------------------");
   
        } catch (IOException e) {
            System.out.println("Error: Could not read course information.");
            System.out.println("Error details: " + e.getMessage());
        }
    }
    
    private static String getStudentName(String matricNo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 4 && values[1].trim().equals(matricNo)) {
                    return values[0].trim(); // Return student name
                }
            }
        }
        return null;
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
}