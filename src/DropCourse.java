import java.io.*;
import java.util.*;

public class DropCourse {

    private static final String STUDENT_FILE = "csv_database/Students.csv";

    public static void drop(String matricNo, Scanner scanner) {

        List<String[]> students = new ArrayList<>();
        boolean studentFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                students.add(line.split(","));
            }
        } catch (IOException e) {
            System.out.println("Error reading Students.csv");
            return;
        }

        for (String[] row : students) {

            if (row.length < 5) continue;

            if (row[1].equals(matricNo)) {
                studentFound = true;

                String coursesData = row[4];

                if (coursesData.equals("()") || coursesData.isEmpty()) {
                    System.out.println("You have no registered courses.");
                    return;
                }

                coursesData = coursesData.substring(1, coursesData.length() - 1); 
                List<String> courses = new ArrayList<>(Arrays.asList(coursesData.split("\\|")));

                System.out.println("\n\n--------------------------------------------------------------------------");
                System.out.println("\n                           REGISTERED COURSE                                  ");
                System.out.println("\n--------------------------------------------------------------------------");
                for (int i = 0; i < courses.size(); i++) {
                    System.out.println((i + 1) + ". " + courses.get(i));
                }

                System.out.print("--------------------------------------------------------------------------");
                System.out.print("\nEnter course code to drop: ");
                String dropCode = scanner.nextLine().trim().toUpperCase();

                if (!courses.contains(dropCode)) {
                    System.out.println("Course not found in your registration!");
                    return;
                }

                courses.remove(dropCode);

                if (courses.isEmpty()) {
                    row[4] = "()";
                } else {
                    row[4] = "(" + String.join("|", courses) + ")";
                }

                System.out.println("Course " + dropCode + " dropped successfully!");
                break;
            }
        }

        if (!studentFound) {
            System.out.println("Student not found!");
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(STUDENT_FILE))) {
            for (String[] row : students) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            System.out.println("Error saving Students.csv");
        }
    }
}
