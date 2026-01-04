import java.io.*;

public class ViewRegisteredCourse {

    private static final String STUDENT_FILE = "csv_database/Students.csv";

    public static void view(String matricNo) {

        boolean studentFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }

                String[] data = line.split(",");

                if (data.length < 5) continue;

                if (data[1].equals(matricNo)) {
                    studentFound = true;

                    String courseData = data[4]; 

                System.out.println("\n\n--------------------------------------------------------------------------");
                System.out.println("\n                           REGISTERED COURSE                                  ");
                System.out.println("\n--------------------------------------------------------------------------");
                

                    if (courseData.equals("()") || courseData.trim().isEmpty()) {
                        System.out.println("You have not registered any courses yet.");
                        return;
                    }

                    courseData = courseData.substring(1, courseData.length() - 1);
                    String[] courses = courseData.split("\\|");

                    for (int i = 0; i < courses.length; i++) {
                        System.out.println((i + 1) + ". " + courses[i]);
                    }

                    return;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading Students.csv");
        }

        if (!studentFound) {
            System.out.println("Student record not found.");
        }
    }
}
