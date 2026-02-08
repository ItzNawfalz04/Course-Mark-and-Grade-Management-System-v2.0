package Lecturer;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ViewStudentsInCourse {
    private static final String COURSES_FILE = Paths.get("csv_database", "Courses.csv").toString();
    private static final String COURSE_ASSG_FILE = Paths.get("csv_database", "CourseAssg.csv").toString();
    private static final String LECTURERS_FILE = Paths.get("csv_database", "Lecturers.csv").toString();
    private static final String STUDENTS_FILE = Paths.get("csv_database", "Students.csv").toString();
    private static final String COURSE_MARKS_DIR = Paths.get("csv_database", "CourseMarks").toString();
    
    public static void view(String workId, Scanner scanner) {
        System.out.println("==============================================================================");
        System.out.println("                     COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                         --- FACULTY OF COMPUTING ---");
        System.out.println("                      UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("==============================================================================");
        System.out.println(">> Lecturer Menu >> View Students in Course");
        System.out.println("------------------------------------------------------------------------------");
        
        try {
            // Get lecturer information
            String lecturerName = getLecturerName(workId);
            
            if (lecturerName == null) {
                System.out.println("Error: Lecturer information not found.");
                return;
            }
            
            // Get assigned courses for this lecturer
            List<String[]> assignedCourses = getAssignedCourses(workId);
            
            if (assignedCourses.isEmpty()) {
                System.out.println("\nYou are not assigned to any courses yet.");
                return;
            }
            
            // Display assigned courses for selection
            System.out.println("\n[SELECT A COURSE]");
            System.out.println("------------------------------------------------------------------------------");
            System.out.printf("%-3s %-40s %-15s %-10s%n", "No.", "Course Name", "Course Code", "Credit Hour");
            System.out.println("------------------------------------------------------------------------------");
            
            for (int i = 0; i < assignedCourses.size(); i++) {
                String[] course = assignedCourses.get(i);
                System.out.printf("%-3d %-40s %-15s %-10s%n", 
                    (i + 1), course[0], course[1], course[2]);
            }
            
            System.out.println("------------------------------------------------------------------------------");
            System.out.print("Enter course number to view students (or 0 to cancel): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                if (choice == 0) {
                    System.out.println("\nOperation cancelled.");
                    return;
                }
                
                if (choice < 1 || choice > assignedCourses.size()) {
                    System.out.println("\nInvalid choice! Please enter a number between 1 and " + assignedCourses.size() + ".");
                    return;
                }
                
                String[] selectedCourse = assignedCourses.get(choice - 1);
                String courseCode = selectedCourse[1];
                String courseName = selectedCourse[0];
                String creditHour = selectedCourse[2];
                
                // Now show students in this course
                displayStudentsInCourse(courseCode, courseName, creditHour, workId);
                
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input! Please enter a number.");
            }
            
        } catch (IOException e) {
            System.out.println("Error: Could not read course information.");
            System.out.println("Error details: " + e.getMessage());
        }
    }
    
    private static void displayStudentsInCourse(String courseCode, String courseName, String creditHour, String workId) throws IOException {
        System.out.println("\n[COURSE INFORMATION]");
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Course Code   : " + courseCode);
        System.out.println("Course Name   : " + courseName);
        System.out.println("Credit Hour   : " + creditHour);
        System.out.println("Assigned to   : " + getLecturerName(workId) + " (Work ID: " + workId + ")");
        System.out.println("------------------------------------------------------------------------------");
        
        // Get all students in this course
        List<StudentRecord> studentRecords = getStudentsInCourse(courseCode);
        
        if (studentRecords.isEmpty()) {
            System.out.println("\nNo students are registered for this course yet.");
            return;
        }
        
        // Display student records
        System.out.println("\n[STUDENT LIST]");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-3s %-15s %-30s %-15s %-15s %-10s %-10s%n", 
            "No.", "Matric No.", "Student Name", "Coursework", "Final Exam", "Total", "Grade");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        
        int studentCount = 1;
        
        for (StudentRecord student : studentRecords) {
            System.out.printf("%-3d %-15s %-30s %-15d %-15d %-10d %-10s%n", 
                studentCount, student.matricNo, student.studentName,
                student.courseworkMark, student.finalExamMark, 
                student.totalMarks, student.grade);
            
            studentCount++;
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }
    
    private static String getLecturerName(String workId) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(LECTURERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 4 && values[1].trim().equals(workId)) {
                    return values[0].trim();
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
    
    private static List<StudentRecord> getStudentsInCourse(String courseCode) throws IOException {
        List<StudentRecord> students = new ArrayList<>();
        String courseFile = Paths.get(COURSE_MARKS_DIR, courseCode + ".csv").toString();
        
        if (!Files.exists(Paths.get(courseFile))) {
            return students;
        }
        
        // First, read all student marks from the course file
        Map<String, int[]> studentMarks = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String matricNo = values[0].trim();
                    try {
                        int coursework = Integer.parseInt(values[1].trim());
                        int finalExam = Integer.parseInt(values[2].trim());
                        studentMarks.put(matricNo, new int[]{coursework, finalExam});
                    } catch (NumberFormatException e) {
                        // Skip invalid marks
                    }
                }
            }
        }
        
        // Now get student names and create StudentRecord objects
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String matricNo = values[1].trim();
                    if (studentMarks.containsKey(matricNo)) {
                        String studentName = values[0].trim();
                        int[] marks = studentMarks.get(matricNo);
                        students.add(new StudentRecord(matricNo, studentName, marks[0], marks[1]));
                    }
                }
            }
        }
        
        // Sort students by matric number
        students.sort(Comparator.comparing(s -> s.matricNo));
        
        return students;
    }
    
    // Inner class to store student record information
    private static class StudentRecord {
        String matricNo;
        String studentName;
        int courseworkMark;
        int finalExamMark;
        int totalMarks;
        String grade;
        
        StudentRecord(String matricNo, String studentName, int courseworkMark, int finalExamMark) {
            this.matricNo = matricNo;
            this.studentName = studentName;
            this.courseworkMark = courseworkMark;
            this.finalExamMark = finalExamMark;
            
            // Calculate total marks
            this.totalMarks = courseworkMark + finalExamMark;
            
            // Calculate grade
            this.grade = calculateGrade(this.totalMarks);
        }
        
        private String calculateGrade(int totalMarks) {
            if (totalMarks >= 90) return "A+";
            if (totalMarks >= 80) return "A";
            if (totalMarks >= 75) return "A-";
            if (totalMarks >= 70) return "B+";
            if (totalMarks >= 65) return "B";
            if (totalMarks >= 60) return "B-";
            if (totalMarks >= 55) return "C+";
            if (totalMarks >= 50) return "C";
            if (totalMarks >= 45) return "C-";
            if (totalMarks >= 40) return "D+";
            if (totalMarks >= 35) return "D";
            return "F";
        }
    }
    
    // Helper method to check if lecturer is assigned to a specific course
    public static boolean isLecturerAssignedToCourse(String workId, String courseCode) throws IOException {
        Set<String> assignedCodes = getAssignedCourseCodes(workId);
        return assignedCodes.contains(courseCode);
    }
}