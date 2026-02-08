package Student;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ViewGradeCGPA {
    private static final String COURSES_FILE = Paths.get("csv_database", "Courses.csv").toString();
    private static final String COURSE_MARKS_DIR = Paths.get("csv_database", "CourseMarks").toString();
    private static final String STUDENTS_FILE = Paths.get("csv_database", "Students.csv").toString();
    
    public static void view(String matricNo) {
        System.out.println("==============================================================================");
        System.out.println("                     COURSE MARK & GRADE MANAGEMENT SYSTEM");
        System.out.println("                         --- FACULTY OF COMPUTING ---");
        System.out.println("                      UNIVERSITI TEKNOLOGI MALAYSIA (UTM)");
        System.out.println("==============================================================================");
        System.out.println(">> Student Menu >> View Grades & CGPA");
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
            System.out.println("Name        : " + studentName);
            System.out.println("Matric No.  : " + matricNo);
            System.out.println("------------------------------------------------------------------------------");
            
            // Get registered courses with marks for this student
            List<CourseResult> courseResults = getCourseResults(matricNo);
            
            if (courseResults.isEmpty()) {
                System.out.println("\nYou are not registered for any courses or no marks available.");
                return;
            }
            
            // Display course results
            System.out.println("\n[COURSE GRADES]");
            System.out.println("-------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-15s %-10s %-10s %-10s %-10s %-10s%n", 
                "Course Name", "Course Code", "Credit", "Coursework", "Final", "Total", "Grade");
            System.out.println("-------------------------------------------------------------------------------------------------------------");
            
            double totalGradePoints = 0;
            int totalCredits = 0;
            int courseCount = 1;
            
            for (CourseResult result : courseResults) {
                System.out.printf("%-40s %-15s %-10d %-10d %-10d %-10d %-10s%n", 
                    result.courseName, result.courseCode, result.creditHours,
                    result.courseworkMark, result.finalExamMark, 
                    result.totalMarks, result.grade);
                
                totalGradePoints += result.gradePoint * result.creditHours;
                totalCredits += result.creditHours;
                courseCount = courseCount + 1;
            }
            
            // Calculate CGPA
            double cgpa = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
            
            // Display summary
            System.out.println("-------------------------------------------------------------------------------------------------------------");
            System.out.println("\n[ACADEMIC SUMMARY]");
            System.out.println("------------------------------------------------------------------------------");
            System.out.printf("%-30s : %d%n", "Total Courses Registered", courseResults.size());
            System.out.printf("%-30s : %d%n", "Total Credit Hours", totalCredits);
            System.out.printf("%-30s : %.2f%n", "Total Grade Points", totalGradePoints);
            System.out.printf("%-30s : %.2f%n", "Cumulative GPA (CGPA)", cgpa);
            System.out.println("------------------------------------------------------------------------------");
            
            // Display grade legend
            System.out.println("\n[GRADE LEGEND]");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Grade  | Grade Point | Marks Range");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("A+     | 4.00        | 90 - 100");
            System.out.println("A      | 4.00        | 80 - 89");
            System.out.println("A-     | 3.67        | 75 - 79");
            System.out.println("B+     | 3.33        | 70 - 74");
            System.out.println("B      | 3.00        | 65 - 69");
            System.out.println("B-     | 2.67        | 60 - 64");
            System.out.println("C+     | 2.33        | 55 - 59");
            System.out.println("C      | 2.00        | 50 - 54");
            System.out.println("C-     | 1.67        | 45 - 49");
            System.out.println("D+     | 1.33        | 40 - 44");
            System.out.println("D      | 1.00        | 35 - 39");
            System.out.println("F      | 0.00        | 0 - 34");
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
    
    private static List<CourseResult> getCourseResults(String matricNo) throws IOException {
        List<CourseResult> results = new ArrayList<>();
        
        // Read all available courses
        List<String[]> allCourses = readAvailableCourses();
        
        // Get marks for each course the student is registered in
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
                        if (values.length >= 3 && values[0].trim().equals(matricNo)) {
                            try {
                                int courseworkMark = Integer.parseInt(values[1].trim());
                                int finalExamMark = Integer.parseInt(values[2].trim());
                                int creditHours = Integer.parseInt(course[2].trim());
                                
                                CourseResult result = new CourseResult(
                                    course[0], courseCode, creditHours, 
                                    courseworkMark, finalExamMark
                                );
                                results.add(result);
                            } catch (NumberFormatException e) {
                                // Skip if marks are not valid numbers
                            }
                            break;
                        }
                    }
                }
            }
        }
        return results;
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
    
    // Inner class to store course results
    private static class CourseResult {
        String courseName;
        String courseCode;
        int creditHours;
        int courseworkMark;
        int finalExamMark;
        int totalMarks;
        String grade;
        double gradePoint;
        
        CourseResult(String courseName, String courseCode, int creditHours, 
                     int courseworkMark, int finalExamMark) {
            this.courseName = courseName;
            this.courseCode = courseCode;
            this.creditHours = creditHours;
            this.courseworkMark = courseworkMark;
            this.finalExamMark = finalExamMark;
            
            // Calculate total marks
            this.totalMarks = courseworkMark + finalExamMark;
            
            // Calculate grade and grade point
            this.grade = calculateGrade(this.totalMarks);
            this.gradePoint = calculateGradePoint(this.grade);
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
        
        private double calculateGradePoint(String grade) {
            switch (grade) {
                case "A+": return 4.00;
                case "A": return 4.00;
                case "A-": return 3.67;
                case "B+": return 3.33;
                case "B": return 3.00;
                case "B-": return 2.67;
                case "C+": return 2.33;
                case "C": return 2.00;
                case "C-": return 1.67;
                case "D+": return 1.33;
                case "D": return 1.00;
                default: return 0.00; // F grade
            }
        }
    }
}