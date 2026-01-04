import java.io.*;
import java.util.*;

public class RegisterCourse {

    private static final String COURSES_FILE = "csv_database/Courses.csv";
    private static final String STUDENT_FILE = "csv_database/Students.csv";

    public static void register(String matricNo, Scanner scanner) {

        Map<String, String[]> availableCourses = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\uFEFF")) line = line.substring(1);
                String[] d = line.split(",");
                if (d.length >= 3) {
                    availableCourses.put(d[1].trim().toUpperCase(),
                            new String[]{d[0].trim(), d[2].trim()});
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Courses.csv");
            return;
        }

      
        System.out.println("\n\n--------------------------------------------------------------------------");
        System.out.println("\n                           REGISTER COURSE                                    ");
        System.out.println("\n--------------------------------------------------------------------------");

        System.out.printf("%-45s %-12s %-6s%n", "\n\nCourse Name", "Course Code", "Credit");
        System.out.println("--------------------------------------------------------------------------");

        for (var e : availableCourses.entrySet()) {
            System.out.printf("%-45s %-12s %-6s%n",
                    e.getValue()[0], e.getKey(), e.getValue()[1]);
        }

        System.out.print("--------------------------------------------------------------------------");
        System.out.print("\nEnter Course Code: ");
        String selectedCode = scanner.nextLine().trim().toUpperCase();

        if (!availableCourses.containsKey(selectedCode)) {
            System.out.println("Invalid course code!");
            return;
        }

        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data[0].equalsIgnoreCase("Name")) {
                    lines.add(line);
                    continue;
                }

                if (data.length >= 5 && data[1].equals(matricNo)) {

                    String raw = data[4]
                            .replace("(", "")
                            .replace(")", "")
                            .trim();

                    Set<String> courses = new LinkedHashSet<>();

                    if (!raw.isEmpty()) {
                        for (String c : raw.split("\\|")) {
                            courses.add(c);
                        }
                    }

                    if (courses.contains(selectedCode)) {
                        System.out.println("You already registered this course!");
                        return;
                    }

                    courses.add(selectedCode);

                    data[4] = "(" + String.join("|", courses) + ")";
                    updated = true;
                }

                lines.add(String.join(",", data));
            }

        } catch (IOException e) {
            System.out.println("Error reading Students.csv");
            return;
        }

        if (!updated) {
            System.out.println("Student not found!");
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(STUDENT_FILE))) {
            for (String l : lines) pw.println(l);
            System.out.println("Course registered successfully!");

        } catch (IOException e) {
            System.out.println("Error writing Students.csv");
        }
    }
}
