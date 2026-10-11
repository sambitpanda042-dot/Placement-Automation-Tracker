import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PlacementCellDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== UNIVERSITY PLACEMENT AUTOMATION TRACKER =====");
            System.out.println("1. View All Registered Students");
            System.out.println("2. View All Incoming Companies");
            System.out.println("3. Run Automated Eligibility Shortlist Check");
            System.out.println("4. Add New Student Profile");
            System.out.println("5. Exit Application");
            System.out.print("Enter your choice (1-5): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: viewStudents(); break;
                case 2: viewCompanies(); break;
                case 3: runShortlistFilter(scanner); break;
                case 4: addNewStudent(scanner); break;
                case 5:
                    System.out.println("Shutting down engine. Goodbye!");
                    scanner.close();
                    System.exit(0);
                default: System.out.println("Invalid selection. Try again.");
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver Class missing: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void viewStudents() {
        String query = "SELECT * FROM Students";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.printf("%-5s | %-15s | %-8s | %-6s | %-15s\n", "ID", "Name", "Branch", "CGPA", "Active Backlogs");
            System.out.println("-------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-8s | %-6.2f | %-15d\n",
                        rs.getInt("student_id"), rs.getString("name"), rs.getString("branch"), rs.getDouble("cgpa"), rs.getInt("active_backlogs"));
            }
        } catch (SQLException e) { System.out.println("Database error: " + e.getMessage()); }
    }

    private static void viewCompanies() {
        String query = "SELECT * FROM Companies";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n-----------------------------------------------------------------------------");
            System.out.printf("%-5s | %-18s | %-25s | %-8s | %-12s\n", "ID", "Company Name", "Job Profile", "Cutoff", "Max Backlogs");
            System.out.println("-----------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d | %-18s | %-25s | %-8.2f | %-12d\n",
                        rs.getInt("company_id"), rs.getString("company_name"), rs.getString("job_profile"), rs.getDouble("min_cgpa"), rs.getInt("max_backlogs"));
            }
        } catch (SQLException e) { System.out.println("Database error: " + e.getMessage()); }
    }

    private static void runShortlistFilter(Scanner scanner) {
        viewCompanies();
        System.out.print("\nEnter the Company ID to run the shortlist check for: ");
        int compId = scanner.nextInt();
        String compQuery = "SELECT company_name, min_cgpa, max_backlogs FROM Companies WHERE company_id = ?";
        String filterQuery = "SELECT name, branch, cgpa, active_backlogs FROM Students WHERE cgpa >= ? AND active_backlogs <= ?";

        try (Connection conn = getConnection(); PreparedStatement compStmt = conn.prepareStatement(compQuery); PreparedStatement filterStmt = conn.prepareStatement(filterQuery)) {
            compStmt.setInt(1, compId);
            ResultSet compRs = compStmt.executeQuery();
            if (compRs.next()) {
                String compName = compRs.getString("company_name");
                double cutoff = compRs.getDouble("min_cgpa");
                int allowedBacklogs = compRs.getInt("max_backlogs");

                System.out.println("\n========================================================");
                System.out.println("AUTOMATED SHORTLIST REPORT FOR: " + compName.toUpperCase());
                System.out.println("Criteria Applied: CGPA >= " + cutoff + " | Max Active Backlogs <= " + allowedBacklogs);
                System.out.println("========================================================");

                filterStmt.setDouble(1, cutoff);
                filterStmt.setInt(2, allowedBacklogs);
                ResultSet studentRs = filterStmt.executeQuery();
                int matchCount = 0;
                System.out.printf("%-18s | %-8s | %-6s | %-15s\n", "Student Name", "Branch", "CGPA", "Active Backlogs");
                System.out.println("--------------------------------------------------------");
                while (studentRs.next()) {
                    matchCount++;
                    System.out.printf("%-18s | %-8s | %-6.2f | %-15d\n",
                            studentRs.getString("name"), studentRs.getString("branch"), studentRs.getDouble("cgpa"), studentRs.getInt("active_backlogs"));
                }
                if (matchCount == 0) { System.out.println("No student profiles matched the criteria requirements."); }
                System.out.println("========================================================");
            } else { System.out.println("Company ID not found."); }
        } catch (SQLException e) { System.out.println("Execution logic failure: " + e.getMessage()); }
    }

    private static void addNewStudent(Scanner scanner) {
        System.out.print("Enter Student Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Branch (e.g., ECE, CSE): ");
        String branch = scanner.next();
        System.out.print("Enter Current Cumulative CGPA: ");
        double cgpa = scanner.nextDouble();
        System.out.print("Enter Count of Active Backlogs: ");
        int backlogs = scanner.nextInt();

        String insertQuery = "INSERT INTO Students (name, branch, cgpa, active_backlogs) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, name);
            pstmt.setString(2, branch);
            pstmt.setDouble(3, cgpa);
            pstmt.setInt(4, backlogs);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student record appended successfully!");
            }
        } catch (SQLException e) {
            // Code 1062 catches the unique key violation thrown by the database server
            if (e.getErrorCode() == 1062) {
                System.out.println("\n[ERROR] Integrity Failure: A student record with this name already exists!");
            } else {
                System.out.println("Failed to insert record: " + e.getMessage());
            }
        }
    }
}