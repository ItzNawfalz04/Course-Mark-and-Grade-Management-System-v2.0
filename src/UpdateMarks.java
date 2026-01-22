import java.io.*;
import java.util.*;

public class UpdateMarks {

    private static final String CSV_PATH = "csv_database/CourseMarks/";
    private static final String STUDENT_FILE = "csv_database/Students.csv"; // Path to Students file

    public static void updateStudentMark(String lecturerWorkId, Scanner scanner) {
        
        System.out.println("\n\n--------------------------------------------------------------------------");
        System.out.println("\n                          UPDATE STUDENT MARKS                            ");
        
        // 1. Select Course
        List<String> myCourses = AssignedCourse.displayAndGetCourses(lecturerWorkId);

        if (myCourses.isEmpty()) return;

        System.out.print("\nEnter number to select course (or 0 to back): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return;
        }

        if (choice == 0) return;
        if (choice < 1 || choice > myCourses.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        String selectedCourseCode = myCourses.get(choice - 1);
        System.out.println("\nSelected Course: " + selectedCourseCode);

        // 2. Open File: csv_database/CourseMarks/[Code].csv
        File courseFile = new File(CSV_PATH + selectedCourseCode + ".csv");
        
        if (!courseFile.exists()) {
            System.out.println("Data file not found at: " + courseFile.getPath());
            System.out.println("No students registered specifically for this course yet.");
            return;
        }

        // ==================================================================================
        // NEW STEP: DISPLAY CURRENT TABLE (Logic from ViewStudentInCourse)
        // ==================================================================================
        displayCurrentMarksTable(courseFile);
        // ==================================================================================

        // 3. Ask for Matric No
        System.out.println("\n(Refer to the table above)");
        System.out.print("Enter Student Matric No to Update (or 0 to cancel): ");
        String matricNo = scanner.nextLine().trim().toUpperCase();

        if (matricNo.equals("0")) return;

        // 4. Process Update (Read File -> Modify Line -> Rewrite File)
        ArrayList<String> Newlines = new ArrayList<>();
        boolean studentFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\uFEFF")) line = line.substring(1);
                
                String[] data = line.split(",");
                if (data.length < 1) { Newlines.add(line); continue; }

                // Match the selected matric No
                if (data[0].trim().equalsIgnoreCase(matricNo)) {
                    studentFound = true;
                    
                    String currentCW = (data.length > 1) ? data[1] : "0";
                    String currentFinal = (data.length > 2) ? data[2] : "0";

                    System.out.println("\n>>> UPDATING: " + data[0].trim());
                    System.out.println("Current Marks -> CW: " + currentCW + " | Final: " + currentFinal);
                    
                    // Get new marks
                    System.out.print("Enter New Course Work Mark (0-60): ");
                    String newCW = scanner.nextLine().trim();
                    
                    System.out.print("Enter New Final Exam Mark (0-40): ");
                    String newFinal = scanner.nextLine().trim();

                    // Simple validation (optional, to avoid errors in View)
                    if (!isNumeric(newCW) || !isNumeric(newFinal)) {
                        System.out.println("Warning: Input is not a number! This might cause errors in View.");
                    }

                    // Prepare updated row
                    ArrayList<String> updatedRowData = new ArrayList<>(Arrays.asList(data));
                    while (updatedRowData.size() <= 2) updatedRowData.add("0"); // Padding if empty

                    updatedRowData.set(1, newCW);    
                    updatedRowData.set(2, newFinal); 

                    Newlines.add(String.join(",", updatedRowData));
                } else {
                    Newlines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file for update.");
            return;
        }

        if (!studentFound) {
            System.out.println("Student with Matric No " + matricNo + " not found in this course!");
            return;
        }

        // 5. Save Changes
        try (PrintWriter pw = new PrintWriter(new FileWriter(courseFile))) {
            for (String l : Newlines) pw.println(l);
            System.out.println("SUCCESS! Marks updated for " + matricNo);
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    // ================= HELPER METHODS TO DISPLAY TABLE =================

    private static void displayCurrentMarksTable(File courseFile) {
        // 1. Load Marks into Map
        Map<String, double[]> studentMarksMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(courseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\uFEFF")) line = line.substring(1);
                String[] data = line.split(",");
                if (data.length >= 1 && !data[0].trim().isEmpty()) {
                    String m = data[0].trim().toUpperCase();
                    double cw = 0, fe = 0;
                    if (data.length > 1) try { cw = Double.parseDouble(data[1].trim()); } catch(Exception e){}
                    if (data.length > 2) try { fe = Double.parseDouble(data[2].trim()); } catch(Exception e){}
                    studentMarksMap.put(m, new double[]{cw, fe});
                }
            }
        } catch (IOException e) { return; } // Silent fail for view only

        // 2. Read Students.csv and Print Table
        System.out.println("\nCurrent Class List:");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-30s %-8s %-8s %-8s %-5s%n", "Matric No", "Name", "CW", "Final", "Total", "Grd");
        System.out.println("-----------------------------------------------------------------------------------------");

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\uFEFF")) line = line.substring(1);
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String name = data[0].trim();
                    String matric = data[1].trim().toUpperCase();

                    if (studentMarksMap.containsKey(matric)) {
                        double[] marks = studentMarksMap.get(matric);
                        double total = marks[0] + marks[1];
                        String grade = getGrade(total);
                        System.out.printf("%-15s %-30s %-8.1f %-8.1f %-8.1f %-5s%n", 
                            matric, name, marks[0], marks[1], total, grade);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading student names.");
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    private static String getGrade(double totalMarks) {
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
        if (totalMarks >= 30) return "D-";
        return "E";
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}