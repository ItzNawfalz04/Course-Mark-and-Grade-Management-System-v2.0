package Lecturer;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class UpdateStudentsMarks {
    private static final String COURSES_FILE = Paths.get("csv_database", "Courses.csv").toString();
    private static final String COURSE_ASSG_FILE = Paths.get("csv_database", "CourseAssg.csv").toString();
    private static final String LECTURERS_FILE = Paths.get("csv_database", "Lecturers.csv").toString();
    private static final String STUDENTS_FILE = Paths.get("csv_database", "Students.csv").toString();
    private static final String COURSE_MARKS_DIR = Paths.get("csv_database", "CourseMarks").toString();
    
    public static void update(String workId, Scanner scanner) {
        System.out.println("==============================================================================");
        System.out.println("                     COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                         --- FACULTY OF COMPUTING ---");
        System.out.println("                      UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("==============================================================================");
        System.out.println(">> Lecturer Menu >> Update Student Marks");
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
            System.out.print("Enter course number to update marks (or 0 to cancel): ");
            
            try {
                int courseChoice = Integer.parseInt(scanner.nextLine());
                
                if (courseChoice == 0) {
                    System.out.println("\nOperation cancelled.");
                    return;
                }
                
                if (courseChoice < 1 || courseChoice > assignedCourses.size()) {
                    System.out.println("\nInvalid choice! Please enter a number between 1 and " + assignedCourses.size() + ".");
                    return;
                }
                
                String[] selectedCourse = assignedCourses.get(courseChoice - 1);
                String courseCode = selectedCourse[1];
                String courseName = selectedCourse[0];
                
                // Now show students in this course for selection
                updateStudentMarksInCourse(courseCode, courseName, workId, scanner);
                
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input! Please enter a number.");
            }
            
        } catch (IOException e) {
            System.out.println("Error: Could not read course information.");
            System.out.println("Error details: " + e.getMessage());
        }
    }
    
    private static void updateStudentMarksInCourse(String courseCode, String courseName, String workId, Scanner scanner) throws IOException {
        System.out.println("\n[COURSE INFORMATION]");
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Course Code   : " + courseCode);
        System.out.println("Course Name   : " + courseName);
        System.out.println("Assigned to   : " + getLecturerName(workId) + " (Work ID: " + workId + ")");
        System.out.println("------------------------------------------------------------------------------");
        
        // Get all students in this course
        List<StudentRecord> studentRecords = getStudentsInCourse(courseCode);
        
        if (studentRecords.isEmpty()) {
            System.out.println("\nNo students are registered for this course yet.");
            return;
        }
        
        // Display student list with current marks
        System.out.println("\n[STUDENT LIST - CURRENT MARKS]");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-3s %-15s %-30s %-15s %-15s %-10s %-10s%n", 
            "No.", "Matric No.", "Student Name", "Coursework", "Final Exam", "Total", "Grade");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        
        for (int i = 0; i < studentRecords.size(); i++) {
            StudentRecord student = studentRecords.get(i);
            System.out.printf("%-3d %-15s %-30s %-15d %-15d %-10d %-10s%n", 
                (i + 1), student.matricNo, student.studentName,
                student.courseworkMark, student.finalExamMark, 
                student.totalMarks, student.grade);
        }
        
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.print("\nEnter student number to update marks (or 0 to cancel): ");
        
        try {
            int studentChoice = Integer.parseInt(scanner.nextLine());
            
            if (studentChoice == 0) {
                System.out.println("\nOperation cancelled.");
                return;
            }
            
            if (studentChoice < 1 || studentChoice > studentRecords.size()) {
                System.out.println("\nInvalid choice! Please enter a number between 1 and " + studentRecords.size() + ".");
                return;
            }
            
            StudentRecord selectedStudent = studentRecords.get(studentChoice - 1);
            
            // Show current marks and allow update
            updateIndividualStudentMarks(courseCode, courseName, selectedStudent, scanner);
            
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input! Please enter a number.");
        }
    }
    
    private static void updateIndividualStudentMarks(String courseCode, String courseName, 
                                                   StudentRecord student, Scanner scanner) {
        System.out.println("\n[UPDATING STUDENTS MARKS]");
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Course        : " + courseName + " (" + courseCode + ")");
        System.out.println("Student       : " + student.studentName);
        System.out.println("Matric No.    : " + student.matricNo);
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Current Marks");
        System.out.println("  Coursework  : " + student.courseworkMark);
        System.out.println("  Final Exam  : " + student.finalExamMark);
        System.out.println("  Total       : " + student.totalMarks + " (" + student.grade + ")");
        System.out.println("------------------------------------------------------------------------------");
        
        // Get updated coursework marks
        System.out.print("\nEnter new Coursework marks (0-70, press Enter to keep current: " + student.courseworkMark + "): ");
        String courseworkInput = scanner.nextLine().trim();
        
        int newCoursework = student.courseworkMark;
        if (!courseworkInput.isEmpty()) {
            try {
                newCoursework = Integer.parseInt(courseworkInput);
                if (newCoursework < 0 || newCoursework > 70) {
                    System.out.println("Invalid marks! Must be between 0 and 70. Keeping current marks.");
                    newCoursework = student.courseworkMark;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Keeping current coursework marks.");
                newCoursework = student.courseworkMark;
            }
        }
        
        // Get updated final exam marks
        System.out.print("Enter new Final Exam marks (0-30, press Enter to keep current: " + student.finalExamMark + "): ");
        String finalExamInput = scanner.nextLine().trim();
        
        int newFinalExam = student.finalExamMark;
        if (!finalExamInput.isEmpty()) {
            try {
                newFinalExam = Integer.parseInt(finalExamInput);
                if (newFinalExam < 0 || newFinalExam > 30) {
                    System.out.println("Invalid marks! Must be between 0 and 30. Keeping current marks.");
                    newFinalExam = student.finalExamMark;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Keeping current final exam marks.");
                newFinalExam = student.finalExamMark;
            }
        }
        
        // Calculate new total and grade
        int newTotal = newCoursework + newFinalExam;
        String newGrade = calculateGrade(newTotal);
        
        // Show preview of changes
        System.out.println("\n------------------------------------------------------------------------------");
        System.out.println("MARK UPDATE PREVIEW:");
        System.out.println("------------------------------------------------------------------------------");
        System.out.printf("%-20s %-15s %-15s %-15s%n", "", "Coursework", "Final Exam", "Total (Grade)");
        System.out.printf("%-20s %-15s %-15s %-15s%n", 
            "Current:", student.courseworkMark, student.finalExamMark, 
            student.totalMarks + " (" + student.grade + ")");
        System.out.printf("%-20s %-15s %-15s %-15s%n", 
            "New:", newCoursework, newFinalExam, newTotal + " (" + newGrade + ")");
        
        if (newCoursework == student.courseworkMark && newFinalExam == student.finalExamMark) {
            System.out.println("\nNo changes made to marks.");
            return;
        }
        
        System.out.print("\nConfirm update? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            // Update the marks in the CSV file
            if (updateMarksInFile(courseCode, student.matricNo, newCoursework, newFinalExam)) {
                System.out.println("\nMarks updated successfully!");
                System.out.println("Student: " + student.studentName);
                System.out.println("New Grade: " + newGrade + " (" + newTotal + " marks)");
            } else {
                System.out.println("\nFailed to update marks. Please try again.");
            }
        } else {
            System.out.println("\nUpdate cancelled.");
        }
    }
    
    private static boolean updateMarksInFile(String courseCode, String matricNo, 
                                           int coursework, int finalExam) {
        String courseFile = Paths.get(COURSE_MARKS_DIR, courseCode + ".csv").toString();
        
        if (!Files.exists(Paths.get(courseFile))) {
            System.out.println("Course file not found: " + courseFile);
            return false;
        }
        
        try {
            // Read all lines and update the specific student's marks
            List<String> lines = new ArrayList<>();
            boolean found = false;
            
            try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.replace("\uFEFF", "").trim();
                    if (line.isEmpty()) continue;
                    
                    String[] values = line.split(",");
                    if (values.length >= 1 && values[0].trim().equals(matricNo)) {
                        // Update this line with new marks
                        line = matricNo + "," + coursework + "," + finalExam;
                        found = true;
                    }
                    lines.add(line);
                }
            }
            
            if (!found) {
                System.out.println("Student not found in course file.");
                return false;
            }
            
            // Write all lines back to the file
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
    
    private static String calculateGrade(int totalMarks) {
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
    }
}