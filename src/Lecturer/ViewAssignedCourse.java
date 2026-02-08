package Lecturer;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ViewAssignedCourse {
    private static final String COURSES_FILE = Paths.get("csv_database", "Courses.csv").toString();
    private static final String COURSE_ASSG_FILE = Paths.get("csv_database", "CourseAssg.csv").toString();
    private static final String LECTURERS_FILE = Paths.get("csv_database", "Lecturers.csv").toString();
    
    public static void view(String workId) {
        System.out.println("==============================================================================");
        System.out.println("                     COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                         --- FACULTY OF COMPUTING ---");
        System.out.println("                      UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("==============================================================================");
        System.out.println(">> Lecturer Menu >> View Assigned Courses");
        System.out.println("------------------------------------------------------------------------------");
        
        try {
            // Get lecturer information
            String lecturerName = getLecturerName(workId);
            
            if (lecturerName == null) {
                System.out.println("Error: Lecturer information not found.");
                return;
            }
            
            // Display lecturer information
            System.out.println("\n[LECTURER INFORMATION]");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Name           : " + lecturerName);
            System.out.println("Work ID        : " + workId);
            System.out.println("------------------------------------------------------------------------------");
            
            // Get assigned courses for this lecturer
            List<String[]> assignedCourses = getAssignedCourses(workId);
            
            if (assignedCourses.isEmpty()) {
                System.out.println("\nYou are not assigned to any courses yet.");
                return;
            }
            
            // Display assigned courses
            System.out.println("\n[ASSIGNED COURSES]");
            System.out.println("------------------------------------------------------------------------------");
            System.out.printf("%-3s %-40s %-15s %-10s%n", "No.", "Course Name", "Course Code", "Credit Hour");
            System.out.println("------------------------------------------------------------------------------");
            
            int totalCredits = 0;
            int courseCount = 1;
            
            for (String[] course : assignedCourses) {
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
            System.out.printf("Total Number of Courses : %d%n", assignedCourses.size());
            System.out.printf("Total Credit Hours      : %d%n", totalCredits);
            System.out.println("------------------------------------------------------------------------------");
            
        } catch (IOException e) {
            System.out.println("Error: Could not read course information.");
            System.out.println("Error details: " + e.getMessage());
        }
    }
    
    private static String getLecturerName(String workId) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(LECTURERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 4 && values[1].trim().equals(workId)) {
                    return values[0].trim(); // Return lecturer name
                }
            }
        }
        return null;
    }
    
    private static List<String[]> getAssignedCourses(String workId) throws IOException {
        List<String[]> assignedCourses = new ArrayList<>();
        
        // Get all course assignments for this lecturer
        Set<String> assignedCourseCodes = getAssignedCourseCodes(workId);
        
        if (assignedCourseCodes.isEmpty()) {
            return assignedCourses;
        }
        
        // Get course details for each assigned course
        List<String[]> allCourses = readAvailableCourses();
        
        for (String[] course : allCourses) {
            String courseCode = course[1];
            if (assignedCourseCodes.contains(courseCode)) {
                assignedCourses.add(course);
            }
        }
        
        return assignedCourses;
    }
    
    private static Set<String> getAssignedCourseCodes(String workId) throws IOException {
        Set<String> assignedCourseCodes = new HashSet<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(COURSE_ASSG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String courseCode = values[0].trim();
                    String lecturerId = values[1].trim();
                    
                    if (lecturerId.equals(workId)) {
                        assignedCourseCodes.add(courseCode);
                    }
                }
            }
        }
        return assignedCourseCodes;
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