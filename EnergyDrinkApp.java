import java.sql.*;
import java.util.Scanner;

public class EnergyDrinkApp {

    // Define the JDBC driver, database URL, and login credentials
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/EnergyDrinksDB";
    static final String USER = "root";
    static final String PASS = "password";

    public static void main(String[] args) {
        // Declare the Scanner, Connection, and Statement objects
        Scanner scanner = new Scanner(System.in);
        Connection conn;
        Statement stmt;

        try {
            // Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection to the database
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create a Statement object for executing SQL queries
            stmt = conn.createStatement();

            // Define the SQL query to create the EnergyDrinks table
            String sql = "CREATE TABLE EnergyDrinks " +
                    "(id INTEGER not NULL, " +
                    " name VARCHAR(255), " +
                    " brand VARCHAR(255), " +
                    " flavor VARCHAR(255), " +
                    " size VARCHAR(255), " +
                    " price FLOAT, " +
                    " PRIMARY KEY ( id ))";

            // Check if the EnergyDrinks table exists
            String checkTableExists = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'EnergyDrinksDB' AND table_name = 'EnergyDrinks'";
            ResultSet rs = stmt.executeQuery(checkTableExists);
            boolean tableExists = false;
            if (rs.next()) {
                tableExists = rs.getInt(1) > 0;
            }

            // Create the EnergyDrinks table if it doesn't exist
            if (!tableExists) {
                stmt.executeUpdate(sql);
                System.out.println("Created EnergyDrinks table in the database.");
            } else {
                System.out.println("=====================================");
                System.out.println("=========     MAIN MENU     =========");
                System.out.println("=====================================\n");
            }

            // Initialize a boolean flag to control the main loop of the application
            boolean exit = false;

            // Main loop of the application
            while (!exit) {
                // Display a menu of options to the user
                System.out.println("Choose an option:");
                System.out.println("1. Create a new energy drink");
                System.out.println("2. Update an existing energy drink");
                System.out.println("3. Remove an energy drink");
                System.out.println("4. Display all energy drinks");
                System.out.println("5. Exit");

                // Read the user's choice from the console
                int option = Integer.parseInt(scanner.nextLine());

                // Switch statement to handle the user's choice
                switch (option) {
                    // Case 1: Create a new energy drink
                    case 1:
                        // Prompt the user for the necessary information
                        System.out.println("Enter the name:");
                        String name = scanner.nextLine();
                        System.out.println("Enter the brand:");
                        String brand = scanner.nextLine();
                        System.out.println("Enter the flavor:");
                        String flavor = scanner.nextLine();
                        System.out.println("Enter the size:");
                        String size = scanner.nextLine();
                        System.out.println("Enter the price:");
                        float price = Float.parseFloat(scanner.nextLine());

                        // Define the SQL query to insert a new row into the EnergyDrinks table
                        sql = "INSERT INTO EnergyDrinks (id, name, brand, flavor, size, price) " +
                                "VALUES (DEFAULT, '" + name + "', '" + brand + "', '" + flavor + "', '" + size + "', " + price + ")";

                        // Execute the SQL query to insert the new energy drink
                        stmt.executeUpdate(sql);
                        System.out.println("New energy drink added to the database.");
                        break;

                    // Case 2: Update an existing energy drink
                    case 2:
                        // Prompt the user for the necessary information
                        System.out.println("Enter the id of the energy drink to update:");
                        int updateId = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter the new name:");
                        String newName = scanner.nextLine();
                        System.out.println("Enter the new brand:");
                        String newBrand = scanner.nextLine();
                        System.out.println("Enter the new flavor:");
                        String newFlavor = scanner.nextLine();
                        System.out.println("Enter the new size:");
                        String newSize = scanner.nextLine();
                        System.out.println("Enter the new price:");
                        float newPrice = Float.parseFloat(System.console().readLine());

                        // Define the SQL query to update the energy drink
                        sql = "UPDATE EnergyDrinks SET name = '" + newName + "', brand = '" + newBrand + "', flavor = '" + newFlavor +
                                "', size = '" + newSize + "', price = " + newPrice + " WHERE id = " + updateId;

                        // Execute the SQL query to update the energy drink
                        stmt.executeUpdate(sql);
                        System.out.println("Energy drink updated in the database.");
                        break;

                    // Case 3: Remove an energy drink
                    case 3:
                        // Prompt the user for the id of the energy drink to remove
                        System.out.println("Enter the id of the energy drink to remove:");
                        int removeId = Integer.parseInt(scanner.nextLine());

                        // Define the SQL query to remove the energy drink
                        sql = "DELETE FROM EnergyDrinks WHERE id = " + removeId;

                        // Execute the SQL query to remove the energy drink
                        stmt.executeUpdate(sql);
                        System.out.println("Energy drink removed from the database.");
                        break;

                    // Case 4: Display all energy drinks
                    case 4:
                        // Define the SQL query to select all energy drinks
                        sql = "SELECT * FROM EnergyDrinks";

                        // Execute the SQL query to select all energy drinks
                        ResultSet resultSet = stmt.executeQuery(sql);

                        // Print the energy drinks
                        while (resultSet.next()) {
                            System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name") + ", Brand: " +
                                    resultSet.getString("brand") + ", Flavor: " + resultSet.getString("flavor") + ", Size: " +
                                    resultSet.getString("size") + ", Price: $" + resultSet.getFloat("price"));
                        }
                        break;

                    // Case 5: Exit
                    case 5:
                        exit = true;
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

            // Close the statement and connection objects
            stmt.close();
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

