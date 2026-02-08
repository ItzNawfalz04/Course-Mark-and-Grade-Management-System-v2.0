package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public abstract class User {
    protected String name;
    protected String id;
    protected String username;
    protected String password;
    protected String role;

    private static final String STUDENTS_FILE = Paths.get("csv_database", "Students.csv").toString();
    private static final String LECTURERS_FILE = Paths.get("csv_database", "Lecturers.csv").toString();
    private static final String ADMINS_FILE = Paths.get("csv_database", "Admin.csv").toString();
    
    public User(String name, String id, String username, String password, String role) {
        this.name = name;
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    
    public static User login(String username, String password) {
        User user;

        user = authenticateFromFile(STUDENTS_FILE, username, password, "student");
        if (user != null) return user;

        user = authenticateFromFile(LECTURERS_FILE, username, password, "lecturer");
        if (user != null) return user;

        user = authenticateFromFile(ADMINS_FILE, username, password, "admin");
        return user;
    }

    private static User authenticateFromFile(String filename, String username, String password, String role) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;

                String[] data = line.split(",");

                if (data.length >= 4) {
                    String name = data[0].trim();
                    String id = data[1].trim();
                    String fileUsername = data[2].trim();
                    String filePassword = data[3].trim();

                    if (fileUsername.equals(username)
                            && filePassword.equals(password)) {

                        switch (role) {
                            case "student":
                                return new Student.Student(
                                        name, id, fileUsername, filePassword);

                            case "lecturer":
                                return new Lecturer.Lecturer(
                                        name, id, fileUsername, filePassword);

                            case "admin":
                                return new Admin.Admin(
                                        name, id, fileUsername, filePassword);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }
        return null;
    }

    // Abstract method for menu display
    public abstract void showMenu(Scanner scanner);
}
