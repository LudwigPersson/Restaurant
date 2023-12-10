import java.sql.*;
import java.util.Scanner;

public class RestaurantManager {

    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = connect();

            boolean quit = false;
            printMenu();
            while (!quit) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 7 -> {
                        System.out.println("\nStänger ner...");
                        quit = true;
                    }
                    case 1 -> addMainCourse();
                    case 2 -> addStarter();
                    case 3 -> showMenu();
                    case 4 -> updateMainCourse();
                    case 5 -> deleteStarter();
                    case 6 -> showCombinedMenu();
                    default -> System.out.println("Ogiltigt val, försök igen.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection connect() {
        String url = "jdbc:sqlite:/Users/ludwigpersson/Desktop/Restaurant.db";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void printMenu() {
        System.out.println("Välkommen till restauranghanteraren");
        System.out.println("1. Lägg till huvudrätt");
        System.out.println("2. Lägg till förrätt");
        System.out.println("3. Visa meny");
        System.out.println("4. Uppdatera huvudrätt");
        System.out.println("5. Ta bort förrätt");
        System.out.println("6. Visa kombinerad meny");
        System.out.println("7. Avsluta");
    }

    private static void addMainCourse() {
        System.out.println("Skriv in namn på huvudrätten: ");
        String name = scanner.nextLine();
        System.out.println("Skriv in beskrivning av huvudrätten: ");
        String description = scanner.nextLine();
        System.out.println("Skriv in pris för huvudrätten: ");
        double price = scanner.nextDouble();

        String sql = "INSERT INTO main_courses (name, description, price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            System.out.println("Huvudrätt tillagd!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForEnterKey();
    }

    private static void addStarter() {
        System.out.println("Skriv in namn på förrätten: ");
        String name = scanner.nextLine();
        System.out.println("Skriv in beskrivning av förrätten: ");
        String description = scanner.nextLine();
        System.out.println("Skriv in pris för förrätten: ");
        double price = scanner.nextDouble();


        System.out.println("Skriv in ID för huvudrätten som förrätten hör till: ");
        int mainCourseId = scanner.nextInt();

        String sql = "INSERT INTO starters (name, description, price, main_course_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, mainCourseId);
            pstmt.executeUpdate();
            System.out.println("Förrätt tillagd!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForEnterKey();
    }

    private static void showMenu() {
        System.out.println("\nHuvudrätter:");
        showDishes("main_courses");

        System.out.println("\nFörrätter:\n");
        showDishes("starters");
        waitForEnterKey();
    }

    private static void showDishes(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Namn: " + rs.getString("name"));
                System.out.println("Beskrivning: " + rs.getString("description"));
                System.out.println("Pris: " + rs.getDouble("price"));
                System.out.println("----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void updateMainCourse() {
        System.out.println("Skriv in ID för den huvudrätt som ska uppdateras: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Skriv in nytt namn för huvudrätten: ");
        String newName = scanner.nextLine();
        System.out.println("Skriv in ny beskrivning för huvudrätten: ");
        String newDescription = scanner.nextLine();
        System.out.println("Skriv in nytt pris för huvudrätten: ");
        double newPrice = scanner.nextDouble();

        String sql = "UPDATE main_courses SET name = ?, description = ?, price = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newDescription);
            pstmt.setDouble(3, newPrice);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            System.out.println("Huvudrätt uppdaterad!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForEnterKey();

    }

    private static void deleteStarter() {
        System.out.println("Skriv in ID för den förrätt som ska tas bort: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM starters WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Förrätt borttagen!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForEnterKey();

    }

    private static void showCombinedMenu() {
        String sql = "SELECT main_courses.name AS main_name, starters.name AS starter_name FROM main_courses " +
                "LEFT JOIN starters ON main_courses.id = starters.main_course_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Huvudrätt: " + rs.getString("main_name"));
                System.out.println("Förrätt: " + rs.getString("starter_name"));
                System.out.println("----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForEnterKey();
    }
    private static void waitForEnterKey() {
        System.out.println("Tryck på Enter för att återgå till menyn...");
        scanner.nextLine();
        printMenu();
    }
}


