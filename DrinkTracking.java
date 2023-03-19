package edu.seminolestate.cis2901.drinktracker;
import java.sql.*;
import java.time.*;
import java. util.*;

public class DrinkTracking {
    static final String URL = "jdbc:mysql://localhost:3306/drinktracker";
    static final String USER = "root";
    static final String PASS = "password";
    private static final Scanner input = new Scanner(System.in);
    private static final int CREATE_SELECTION = 1;
    private static final int REMOVE_SELECTION = 2;
    private static final int UPDATE_SELECTION = 3;
    private static final int DISPLAY_SELECTION = 4;
    private static final int EXIT = 5;
    private static final int BACK = 6;


    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("========= DRINK TRACKER 1.5 =========");
        System.out.println("=====================================\n");

        mainSelection();

        input.close();
    } // Ends Main


    // GetInt filter
    public static int getInt(String prompt) {
        String userValue = null;
        int validInt = -1;

        do {
            System.out.println(prompt);
            try{
                userValue = input.nextLine();
                validInt = Integer.parseInt(userValue);
                if (validInt <= 0 || userValue.length() >= 7) {
                    System.err.println("Number must be greater than 0 and/or under 6 digits long.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Re-enter valid number");
            }
        } while (validInt <= 0 || userValue.length() >= 7);

        return validInt;
    }

    // GetString filter
    public static String getString(String prompt) {
        String userValue;
        do {
            System.out.println(prompt);
            userValue = input.nextLine();
            if (userValue == null || userValue.length() < 1 || userValue.length() > 50) {
                System.err.println("You must enter a value up to 50 characters.");
                mainSelection();
            }
        } while (userValue == null || userValue.length() < 1 || userValue.length() > 50);

        return userValue;
    } // End of GetString

    private static int mainSelection() {
        int userResponse = 0;
        String userInput;

        do {
            System.out.println("=====================================");
            System.out.println("=========     MAIN MENU     =========");
            System.out.println("=====================================\n");
            System.out.println("Enter your choice: ");
            System.out.println(CREATE_SELECTION + ". Drinks");
            System.out.println(REMOVE_SELECTION + ". Stores");
            System.out.println(UPDATE_SELECTION + ". Orders");
            System.out.println(DISPLAY_SELECTION + ". Deliveries");
            System.out.println(EXIT + ". Exit\n");

            try {
                userInput = input.nextLine();
                userResponse = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.err.println("Must use a valid number to proceed");
            }

            if (userResponse < CREATE_SELECTION || userResponse  > EXIT){
                System.err.println("Invalid value. Enter a value: " + CREATE_SELECTION + " - " + EXIT);
            }

            if (userResponse == EXIT) {
                System.exit(-1);
            }

            // Drink menu
            if (userResponse == CREATE_SELECTION) {
                PreparedStatement preparedStatement = null;
                Connection connection = null;
                Statement statement = null;

                do {
                    System.out.println("=====================================");
                    System.out.println("=========       DRINKS      =========");
                    System.out.println("=====================================\n");
                    System.out.println("Enter your choice: ");
                    System.out.println(CREATE_SELECTION + ". Create a new drink");
                    System.out.println(REMOVE_SELECTION + ". Remove an existing drink.");
                    System.out.println(UPDATE_SELECTION + ". Edit an existing drink.");
                    System.out.println(DISPLAY_SELECTION + ". View an existing drink.");
                    System.out.println(BACK + ". Back");

                    try {
                        userInput = input.nextLine();
                        userResponse = Integer.parseInt(userInput);
                    } catch (NumberFormatException e) {
                        System.err.println("Must use a valid number to proceed");
                    }

                    if (userResponse < CREATE_SELECTION || userResponse  > BACK){
                        System.err.println("Invalid value. Enter a value: " + CREATE_SELECTION + ", " + REMOVE_SELECTION +
                                ", " + UPDATE_SELECTION + ", " + DISPLAY_SELECTION + ", or " + BACK);
                    }

                    if (userResponse == BACK) {
                        mainSelection();
                    }
                } while (userResponse < CREATE_SELECTION || userResponse  > BACK);

                switch (userResponse) {

                    case CREATE_SELECTION:
                        /* 1. Gets user input from Scanner and filters through getInt and getString. */
                        int tempID = getInt("Enter the Product ID: ");
                        String tempName = getString("Enter Product's Name: ");
                        String tempDesc = getString("Enter Product's description: ");

                        /* This will filter the boolean input for non-true/false responses */
                        Boolean tempAvail = null;
                        do {
                            System.out.println("(true OR false): This product is available for sale?");
                            try {
                                tempAvail = input.nextBoolean();
                            } catch (InputMismatchException e) {
                                System.err.println("Please enter only 'true' OR 'false'.");
                                input.next();
                            }
                        } while (tempAvail == null);


                        input.nextLine(); // Flushes scanner
                        Timestamp tempStamp = Timestamp.from(Instant.now());
                        System.out.println("Product entry as " + tempID + " has been timestamped.\n");
                        int tempStock = getInt("Enter Product's quantity: ");

                        /* 2. Creates SQL statement with user input */
                        String createSql = "INSERT INTO `products` (`product_id`, `product_name`, `description`,"
                                + " `product_available`, `product_moved`, `product_stock`) VALUES (?, ?, ?, ?, ?, ?);";

                        try {
                            connection = DriverManager.getConnection(URL, USER, PASS);
                            statement = connection.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        /* 3. Set values with user input */
                        try {
                            if (connection != null) {
                                preparedStatement = connection.prepareStatement(createSql);
                                preparedStatement.setInt(1, tempID);
                                preparedStatement.setString(2, tempName);
                                preparedStatement.setString(3, tempDesc);
                                preparedStatement.setBoolean(4, tempAvail);
                                preparedStatement.setTimestamp(5, tempStamp);
                                preparedStatement.setInt(6, tempStock);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        /* 4. Submits and executes the query */
                        try {
                            preparedStatement.executeUpdate();
                            System.out.println("Product " + tempID + " has been created on the database.\n");
                        } catch (SQLIntegrityConstraintViolationException e) {
                            System.err.println("Please try again with different 'Product ID'. " + tempID + " already exists.\n");
                            mainSelection();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        mainSelection();
                        break;


                    case REMOVE_SELECTION:
                        /* Here we implement code to remove a drink from the database and update */
                        String decision;
                        boolean choice = true;

                        while (choice) {
                            // 1. Obtain product ID from user input then confirm selection.
                            int tempRemove = getInt("Enter the Product ID you'd like to remove: ");
                            System.out.println("Are you sure you want to remove: " + tempRemove + " (yes OR no)");
                            decision = input.nextLine();

                            // 2. Switch case to loop if user does not want to proceed.
                            switch(decision) {
                                case "yes":
                                    choice = true;
                                    String deleteSql = "DELETE FROM `products` WHERE `product_id`=?;";
                                    try {
                                        connection = DriverManager.getConnection(URL, USER, PASS);
                                        statement = connection.createStatement();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    ResultSet result;

                                    try {
                                        result = statement.executeQuery("SELECT * FROM `products` WHERE `product_id`=" + tempRemove +";");
                                        if (!result.isBeforeFirst()) {
                                            System.err.println("There is no Product " + tempRemove + " in the database.");
                                            System.out.println("Returning to menu selection.\n");
                                            break;
                                        } else {
                                            try {
                                                preparedStatement = connection.prepareStatement(deleteSql);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                preparedStatement.setInt(1, tempRemove);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    // 3. Submits and executes the query
                                    try {
                                        preparedStatement.executeUpdate();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("Product " + tempRemove + " has been removed from database.\n");
                                    break;

                                case "no":
                                    choice = false;
                                    System.out.println("OK. Returning to menu selection.\n");
                                    break;

                                default:
                                    System.err.println("Invalid entry.");
                                    System.out.println("Returning to menu selection.\n");
                            }
                            break;
                        }
                        break;

                    case UPDATE_SELECTION:
                        String updateDecision;
                        boolean updateChoice = true;

                        while (updateChoice) {
                            int updateID = getInt("Enter the Product ID you'd like to update: ");
                            System.out.println("Are you sure you want to update: " + updateID + " (yes OR no)");
                            updateDecision = input.nextLine();

                            switch (updateDecision) {
                                case "yes":
                                    // ID validation starts
                                    try {
                                        connection = DriverManager.getConnection(URL, USER, PASS);
                                        statement = connection.createStatement();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    ResultSet result = null;

                                    try {
                                        result = statement.executeQuery("SELECT * FROM `products` WHERE `product_id`=" + updateID + ";");
                                        if (!result.isBeforeFirst()) {
                                            System.err.println(updateID + " is invalid. This ID does not exist.\n");
                                            // ID validation ends

                                        } else {
                                            /* 1. Gets user input and filters */
                                            String updateName = getString("Enter the " + updateID + "'s new name: ");
                                            String updateDesc = getString("Enter the " + updateID + "'s new description: ");

                                            Boolean updateAvail = null;
                                            System.out.println("(true OR false): Updated product '" + updateName + "' is available for sale?");
                                            do {
                                                try {
                                                    updateAvail = input.nextBoolean();
                                                } catch (InputMismatchException e) {
                                                    System.err.println("Please enter only 'true' OR 'false'.");
                                                    input.next();
                                                }
                                            } while (updateAvail == null);

                                            input.nextLine(); // Flushes scanner
                                            Timestamp updateStamp = Timestamp.from(Instant.now());
                                            int updateStock = getInt("Enter the " + updateID + "'s new quantity: ");

                                            /* 2. Updates SQL statement with user input
                                             * */
                                            String updateSql = "UPDATE `products` SET `product_name` = ?, `description` = ?, " +
                                                    "`product_available` = ?, `product_moved` = ?, `product_stock` = ? WHERE `product_id` = ?";

                                            /* 3. Set values with user input
                                             * */
                                            try {
                                                preparedStatement = connection.prepareStatement(updateSql);
                                                preparedStatement.setString(1, updateName);
                                                preparedStatement.setString(2, updateDesc);
                                                preparedStatement.setBoolean(3, updateAvail);
                                                preparedStatement.setTimestamp(4, updateStamp);
                                                preparedStatement.setInt(5, updateStock);
                                                preparedStatement.setInt(6, updateID);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                            /* 4. Submits and executes the update
                                             * */
                                            try {
                                                preparedStatement.executeUpdate();
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                            System.out.println("Product " + updateID + " has been updated.\n");
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                case "no":
                                    updateChoice = false;
                                    System.out.println("OK. Returning to menu selection.\n");
                                    mainSelection();
                                    break;
                            }
                            break;
                        }
                        break;

                    case DISPLAY_SELECTION:
                        ResultSet result = null;
                        String selectDecision;
                        boolean selectChoice = true;

                        while (selectChoice) {
                            int selectID = getInt("Enter the Product ID for the drink you want to view: ");
                            System.out.println("Are you sure you want to view " + selectID + "? (yes OR no)");
                            selectDecision = input.nextLine();

                            switch (selectDecision) {
                                case "yes":
                                    // ID validation
                                    try {
                                        connection = DriverManager.getConnection(URL, USER, PASS);
                                        statement = connection.createStatement();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    String query = "SELECT `product_id`, `product_name`, " +
                                            "`description`, `product_available`, `product_moved`, `product_stock` " +
                                            "FROM `products` WHERE `product_id`=" + selectID + ";";

                                    try {
                                        result = statement.executeQuery(query);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        result = statement.executeQuery("SELECT * FROM `products` WHERE `product_id` = "
                                                + selectID + ";");
                                        if (!result.isBeforeFirst()) {
                                            System.err.println(selectID + " is invalid. This ID does not exist.\n");
                                        } while (result.next()) {
                                            System.out.print("ID: " + result.getInt("product_id"));
                                            System.out.print("\nName: " + result.getString("product_name"));
                                            System.out.print("\nDescription: " + result.getString("description"));
                                            System.out.print("\nAvailable? (1 = Yes, 0 = No): " + result.getInt("product_available"));
                                            System.out.print("\nLast modified: " + result.getTimestamp("product_moved"));
                                            System.out.println("\nQuantity: " + result.getInt("product_stock"));
                                            System.out.println("");

                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                case "no":
                                    selectChoice = false;
                                    System.out.println("Returning to menu selection.\n");
                                    break;
                            }
                            break;
                        }

                    case BACK:
                        mainSelection();
                        break;
                }


            } // End of drink menu

            // Store menu
            if (userResponse == REMOVE_SELECTION) {
                PreparedStatement preparedStatement = null;
                Connection connection = null;
                Statement statement = null;

                do {
                    System.out.println("=====================================");
                    System.out.println("=========       STORES      =========");
                    System.out.println("=====================================\n");
                    System.out.println("Enter your choice: ");
                    System.out.println(CREATE_SELECTION + ". Add a store");
                    System.out.println(REMOVE_SELECTION + ". Remove a store");
                    System.out.println(UPDATE_SELECTION + ". Modify a store");
                    System.out.println(DISPLAY_SELECTION + ". Display a store's details");
                    System.out.println(BACK + ". Back");

                    try {
                        userInput = input.nextLine();
                        userResponse = Integer.parseInt(userInput);
                    } catch (NumberFormatException e) {
                        System.err.println("Must use a valid number to proceed");
                    }

                    if (userResponse < CREATE_SELECTION || userResponse  > BACK){
                        System.err.println("Invalid value. Enter a value: " + CREATE_SELECTION + ", " + REMOVE_SELECTION +
                                ", " + UPDATE_SELECTION + ", " + DISPLAY_SELECTION + ", or " + BACK);
                    }

                    if (userResponse == BACK) {
                        mainSelection();
                    }
                } while (userResponse < CREATE_SELECTION || userResponse  > BACK);

                switch (userResponse) {
                    case CREATE_SELECTION:
                        /* 1. Gets user input from Scanner and filters through getInt and getString. */
                        int tempID = getInt("Enter the store's ID number: ");
                        String tempName = getString("Enter the store's name: ");
                        Timestamp tempStamp = Timestamp.from(Instant.now());
                        int tempStock = getInt("Enter the store's initial quantity stock: ");

                        Boolean restocked = null;
                        do {
                            System.out.println("(true OR false): Store #" + tempID + " requires daily restocking?");
                            try {
                                restocked = input.nextBoolean();
                            } catch (InputMismatchException e) {
                                System.err.println("Please enter only 'true' OR 'false'.");
                                input.next();
                            }
                        } while (restocked == null);

                        input.nextLine(); // Flushes scanner

                        /* 2. Creates SQL statement with user input */
                        String createSql = "INSERT INTO `stores` (`store_id`, `store_name`, `last_updated`, `store_stock`, `restocked`)" +
                                " VALUES (?, ?, ?, ?, ?)";

                        try {
                            connection = DriverManager.getConnection(URL, USER, PASS);
                            statement = connection.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        /* 3. Set values with user input */
                        try {
                                preparedStatement = connection.prepareStatement(createSql);
                                preparedStatement.setInt(1, tempID);
                                preparedStatement.setString(2, tempName);
                                preparedStatement.setTimestamp(3, tempStamp);
                                preparedStatement.setInt(4, tempStock);
                                preparedStatement.setBoolean(5, restocked);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        /* 4. Submits and executes the query */
                        try {
                            preparedStatement.executeUpdate();
                            System.out.println("Store '" + tempName + "' / #" + tempID + " has been created on the database.\n");
                        } catch (SQLIntegrityConstraintViolationException e) {
                            System.err.println("Please try again with different 'Store ID number'. " + tempID + " already exists.\n");
                            mainSelection();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        mainSelection();
                        break;

                    case REMOVE_SELECTION:
                        String decision;
                        boolean choice = true;

                        while (choice) {
                            // 1. Obtain product ID from user input then confirm selection.
                            int tempRemove = getInt("Enter the store's ID number you'd like to remove: ");
                            System.out.println("Store#" + tempRemove + ", is this the location you'd like to remove? (yes OR no)");
                            decision = input.nextLine();

                            // 2. Switch case to loop if user does not want to proceed.
                            switch(decision) {
                                case "yes":
                                    choice = true;
                                    String deleteSql = "DELETE FROM `stores` WHERE `store_id`=?;";
                                    ResultSet result = null;

                                    try {
                                        connection = DriverManager.getConnection(URL, USER, PASS);
                                        Statement stmt = connection.createStatement();
                                        result = stmt.executeQuery("SELECT * FROM `stores` WHERE `store_id`=" + tempRemove +";");
                                        if (!result.isBeforeFirst()) {
                                            System.err.println("There is no store #" + tempRemove + " in the database.");
                                            System.out.println("Returning to menu selection.\n");
                                            mainSelection();
                                            break;
                                        } else {
                                            try {
                                                preparedStatement = connection.prepareStatement(deleteSql);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                preparedStatement.setInt(1, tempRemove);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    // 3. Submits and executes the query
                                    try {
                                        preparedStatement.executeUpdate();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("Store #" + tempRemove + " has been removed from database.\n");
                                    mainSelection();
                                    break;

                                case "no":
                                    choice = false;
                                    System.out.println("OK. Returning to menu selection.\n");
                                    mainSelection();

                                default:
                                    System.err.println("Invalid entry.");
                                    System.out.println("Returning to menu selection.\n");
                                    mainSelection();
                            }
                            break;
                        }
                        break;

                    case UPDATE_SELECTION:
                        String updateDecision;
                        try {
                            connection = DriverManager.getConnection(URL, USER, PASS);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        try {
                            statement = connection.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        boolean updateChoice = true;

                        while (updateChoice) {
                            int updateID = getInt("Enter the store's ID number you'd like to update: ");
                            System.out.println("You'd like to update info for Store#" + updateID + "? (yes OR no)");
                            updateDecision = input.nextLine();

                            switch (updateDecision) {
                                case "yes":
                                    // ID validation starts
                                    ResultSet result = null;

                                    try {
                                        result = statement.executeQuery("SELECT * FROM `stores` WHERE `store_id`=" + updateID + ";");
                                        if (!result.isBeforeFirst()) {
                                            System.err.println(updateID + " is invalid. This ID does not exist.\n");
                                            System.out.println("Returning to menu selection.\n");
                                            mainSelection();
                                            // ID validation ends

                                        } else {
                                            String updateName = getString("Enter store #" + updateID + "'s new name: ");
                                            Timestamp updateStamp = Timestamp.from(Instant.now());



                                            Boolean updateRestock = null;
                                            System.out.println("(true OR false): '" + updateName + "' now requires daily restocking?");
                                            do {
                                                try {
                                                    updateRestock = input.nextBoolean();
                                                } catch (InputMismatchException e) {
                                                    System.err.println("Please enter only 'true' OR 'false'.");
                                                    input.next();
                                                }
                                            } while (updateRestock == null);

                                            input.nextLine(); // Flushes scanner
                                            int updateStock = getInt("Enter store #" + updateID + "'s new quantity: ");

                                            String updateSql = "UPDATE `stores` SET `store_name` = ?, " +
                                                    "`last_updated` = ?, `store_stock` = ?, `restocked` = ? WHERE `store_id` = ?";

                                            try {
                                                preparedStatement = connection.prepareStatement(updateSql);
                                                preparedStatement.setString(1, updateName);
                                                preparedStatement.setTimestamp(2, updateStamp);
                                                preparedStatement.setInt(3, updateStock);
                                                preparedStatement.setBoolean(4, updateRestock);
                                                preparedStatement.setInt(5, updateID);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                preparedStatement.executeUpdate();
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }

                                            System.out.println("Store #" + updateID + " '" + updateName + "' has been updated.\n");
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    mainSelection();

                                case "no":
                                    updateChoice = false;
                                    System.out.println("OK. Returning to menu selection.\n");
                                    mainSelection();
                                    break;
                            }
                            break;
                        }
                        break;

                    case DISPLAY_SELECTION:
                        String selectDecision;
                        try {
                            connection = DriverManager.getConnection(URL, USER, PASS);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        try {
                            statement = connection.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        boolean selectChoice = true;

                        while (selectChoice) {
                            int selectID = getInt("Enter the ID number for the store you'd like to view: ");
                            System.out.println("Are you sure you want to view Store#" + selectID + "? (yes OR no)");
                            selectDecision = input.nextLine();

                            switch (selectDecision) {

                                case "yes":
                                    try {
                                        ResultSet result = statement.executeQuery("SELECT * FROM `stores` WHERE `store_id` = "
                                                + selectID + ";");
                                        if (!result.isBeforeFirst()) {
                                            System.err.println(selectID + " is invalid. This ID does not exist.\n");
                                        } while (result.next()) {
                                            System.out.print("ID: " + result.getInt("store_id"));
                                            System.out.print("\nName: " + result.getString("store_name"));
                                            System.out.print("\nLast modified: " + result.getTimestamp("last_updated"));
                                            System.out.print("\nDescription: " + result.getInt("store_stock"));
                                            System.out.print("\nDaily restocking required? (1 = Yes, 0 = No): " + result.getInt("restocked"));
                                            System.out.println("\n----");
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                case "no":
                                    selectChoice = false;
                                    System.out.println("Returning to menu selection.\n");
                                    mainSelection();
                                    break;
                            }
                            break;
                        }
                        break;

                    case BACK:
                        mainSelection();
                }
            } // End of store menu

            // Orders menu
            if (userResponse == UPDATE_SELECTION) {
                PreparedStatement preparedStatement = null;
                Connection connection;
                ResultSet result;
                Statement statement = null;
                String decision = "";
                String storeName = "";

                do {
                    System.out.println("=====================================");
                    System.out.println("=========       ORDERS      =========");
                    System.out.println("=====================================\n");
                    System.out.println("Enter your choice: ");
                    System.out.println(CREATE_SELECTION + ". Create new order");
                    System.out.println(REMOVE_SELECTION + ". Delete existing order");
                    System.out.println(UPDATE_SELECTION + ". Edit an order");
                    System.out.println(DISPLAY_SELECTION + ". View existing order");
                    System.out.println(BACK + ". Back");

                    try {
                        userInput = input.nextLine();
                        userResponse = Integer.parseInt(userInput);
                    } catch (NumberFormatException e) {
                        System.err.println("Must use a valid number to proceed");
                    }

                    if (userResponse < CREATE_SELECTION || userResponse  > BACK){
                        System.err.println("Invalid value. Enter a value: " + CREATE_SELECTION + ", " + REMOVE_SELECTION +
                                ", " + UPDATE_SELECTION + ", " + DISPLAY_SELECTION + ", or " + BACK);
                    }

                    if (userResponse == BACK) {
                        mainSelection();
                    }
                } while (userResponse < CREATE_SELECTION || userResponse  > BACK);

                switch (userResponse) {
                    case CREATE_SELECTION:
                        int productID = getInt("Please enter the product ID# you're ordering: ");
                        String productName = "";
                        // ID validation
                        try {
                            connection = DriverManager.getConnection(URL, USER, PASS);
                            statement = connection.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            result = statement.executeQuery("SELECT * FROM `products` WHERE `product_id`=" + productID + ";");
                            if (!result.isBeforeFirst()) {
                                System.err.println(productID + " is invalid. This ID does not exist.");
                                mainSelection();
                            } while (result.next()) {
                                // Obtains column title from DB
                                productName = result.getString("product_name");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Are you making an order for '" + productName + "'? (yes OR no)");
                        decision = input.nextLine();

                        switch (decision) {
                            case "yes":
                                int orderQTY = getInt("Please enter the quantity for this order: ");
                                Timestamp orderDate = Timestamp.from(Instant.now());
                                Boolean orderComp = false;
                                int checkStore = getInt("Please enter the store ID# for this order: ");
                                //  Need to "Please enter the store ID# for this order: ", ID validation,
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    result = statement.executeQuery("SELECT * FROM `stores` WHERE `store_id`=" + checkStore + ";");
                                    if (!result.isBeforeFirst()) {
                                        System.err.println(productID + " is invalid. This ID does not exist.");
                                        mainSelection();
                                    }
                                    while (result.next()) {
                                        // Obtains column title from DB
                                        storeName = result.getString("store_name");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                //  verify if correct store Y/N, then result.getString for store_name == store_ordered
                                // End script

                                String setDelivery = null;
                                try {
                                    System.out.println("Store: " + storeName + " -- Is there a delivery already scheduled for this order? (yes OR no)");
                                    setDelivery = input.nextLine();
                                } catch (InputMismatchException e) {
                                    System.err.println("Please enter only 'yes' or 'no'.");
                                    input.next();
                                }

                                switch (setDelivery) {
                                    case "yes":
                                        int checkDel = getInt("Please enter the delivery # you'd like to assign to this order:");
                                        // ID validation
                                        try {
                                            connection = DriverManager.getConnection(URL, USER, PASS);
                                            statement = connection.createStatement();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            result = statement.executeQuery("SELECT * FROM `deliveries` WHERE `delivery_id`=" + checkDel + ";");
                                            if (!result.isBeforeFirst()) {
                                                System.err.println(productID + " is invalid. This ID does not exist.");
                                                mainSelection();
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        // Script to create order WITH delivery #
                                        String orderSql = "INSERT INTO `orders`(`delivery_id`, `product_id`, `order_qty`, `order_date`, `order_completed`, `store_ordered`) VALUES (?,?,?,?,?,?)";

                                        try {
                                            connection = DriverManager.getConnection(URL, USER, PASS);
                                            preparedStatement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                                            preparedStatement.setInt(1, checkDel);
                                            preparedStatement.setInt(2, productID);
                                            preparedStatement.setInt(3, orderQTY);
                                            preparedStatement.setTimestamp(4, orderDate);
                                            preparedStatement.setBoolean(5, orderComp);
                                            preparedStatement.setString(6, storeName);
                                            preparedStatement.executeUpdate();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("Your order has been created.");
                                        mainSelection();

                                    case "no":
                                        // Script to create order WITHOUT delivery #
                                        orderSql = "INSERT INTO `orders`(`product_id`, `order_qty`, `order_date`, `order_completed`, `store_ordered`) VALUES (?,?,?,?,?)";
                                        try {
                                            connection = DriverManager.getConnection(URL, USER, PASS);
                                            preparedStatement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                                            preparedStatement.setInt(1, productID);
                                            preparedStatement.setInt(2, orderQTY);
                                            preparedStatement.setTimestamp(3, orderDate);
                                            preparedStatement.setBoolean(4, orderComp);
                                            preparedStatement.setString(5, storeName);
                                            preparedStatement.executeUpdate();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("Your order has been created.");
                                        mainSelection();
                                }
                            case "no":
                                System.out.println("OK. Returning to menu selection.\n");
                                mainSelection();
                            default:
                                System.err.println("Invalid entry. Returning to menu selection.\n");
                                mainSelection();
                        }
                        break;
                    case REMOVE_SELECTION:
                        String cancelSql = "DELETE FROM `orders` WHERE `order_id`=?;";
                        int cancelID = getInt("Please enter the order ID# you'd like to cancel: ");
                        System.err.println("CONFIRM: You'd like to cancel order#" + cancelID + "? (yes OR no)");
                        decision = input.nextLine();

                        switch(decision) {
                            case "yes":
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();
                                    result = statement.executeQuery("SELECT * FROM `orders` WHERE `order_id`=" + cancelID + ";");
                                    if(!result.isBeforeFirst()) {
                                        System.err.println("There is no order #" + cancelID + " in the database.");
                                        System.out.println("Returning to menu selection.\n");
                                        mainSelection();
                                    } else {
                                        try {
                                            preparedStatement = connection.prepareStatement(cancelSql);
                                            preparedStatement.setInt(1,cancelID);
                                            preparedStatement.executeUpdate();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Order #" + cancelID + " has been cancelled.\n");
                                mainSelection();
                            case "no":
                                System.out.println("OK. Returning to menu selection.");
                                mainSelection();
                            default:
                                System.out.println("Invalid entry. Returning to menu selection.");
                                mainSelection();
                        }
                    case UPDATE_SELECTION:
                        int editID = getInt("Please enter the order ID# you'd like to modify:");
                        String updateSql = "UPDATE `orders` SET `order_completed`= ?,`order_qty`= ? WHERE `order_id`= " + editID +";";
                        System.err.println("CONFIRM: You'd like to modify order#" + editID + "? (yes OR no)");
                        decision = input.nextLine();

                        switch (decision) {
                            case  "yes":
                                Boolean orderCompleted = null;
                                // ID Validation
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();
                                    result = statement.executeQuery("SELECT * FROM `orders` WHERE `order_id`=" + editID +";");
                                    if (!result.isBeforeFirst()) {
                                        System.err.println("There is no order #" + editID + " in the database.");
                                        System.out.println("Returning to menu selection.\n");
                                        mainSelection();
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                do {
                                    System.out.println("(true OR false): This order has been completed.");
                                    try {
                                        orderCompleted = input.nextBoolean();
                                    } catch (InputMismatchException e) {
                                        System.err.println("Please enter only 'true' or 'false'.");
                                        input.next();
                                    }
                                } while (orderCompleted == null);
                                input.nextLine();
                                int orderQty = getInt("Please update the quantity for order#" + editID + ": ");

                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    preparedStatement = connection.prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS);
                                    preparedStatement.setBoolean(1, orderCompleted);
                                    preparedStatement.setInt(2, orderQty);
                                    preparedStatement.executeUpdate();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Order# " + editID + " has been updated.\n");
                                mainSelection();
                            case "no":
                                System.out.println("OK. Returning to menu selection.\n");
                                mainSelection();
                            default:
                                System.out.println("Invalid entry. Returning to menu selection.\n");
                                mainSelection();
                        }
                    case DISPLAY_SELECTION:
                        int displayID = getInt("Please enter the order# you wish you view: ");
                        System.err.println("CONFIRM: You would like to view order# " + displayID + "? (yes OR no)");
                        decision = input.nextLine();

                        switch (decision) {
                            case "yes":
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();
                                    result = statement.executeQuery("SELECT * FROM `orders` WHERE `order_id`=" + displayID + ";");
                                    if (!result.isBeforeFirst()) {
                                        System.err.println(displayID + " is invalid. This ID does not exist.\n");
                                    } while (result.next()) {
                                        System.out.print("\nOrder#: " + result.getInt("order_id"));
                                        System.out.print("\nDelivery#: " + result.getInt("delivery_id"));
                                        System.out.print("\nProduct#: " + result.getInt("product_id"));
                                        System.out.print("\nQuantity: " + result.getInt("order_qty"));
                                        System.out.print("\nDate: " + result.getTimestamp("order_date"));
                                        System.out.print("\nStore Name: " + result.getString("store_ordered"));
                                        System.out.print("\nCompleted?: " + result.getBoolean("order_completed"));
                                        System.out.println("\n----");
                                        }
                                    } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            case "no":
                                System.out.println("OK. Returning to menu selection.\n");
                                mainSelection();
                            default:
                                System.out.println("Invalid entry. Returning to menu selection.\n");
                                mainSelection();
                        }
                    case BACK:
                        mainSelection();
                        break;
                }

            } // End of orders menu

            // Delivery menu
            if (userResponse == DISPLAY_SELECTION) {
                PreparedStatement preparedStatement = null;
                Connection connection = null;
                Statement statement = null;

                do {
                    System.out.println("=====================================");
                    System.out.println("=========     DELIVERIES    =========");
                    System.out.println("=====================================\n");
                    System.out.println("Enter your choice: ");
                    System.out.println(CREATE_SELECTION + ". Assign a delivery");
                    System.out.println(REMOVE_SELECTION + ". Cancel a delivery");
                    System.out.println(UPDATE_SELECTION + ". Edit an existing delivery");
                    System.out.println(DISPLAY_SELECTION + ". Display delivery details");
                    System.out.println(BACK + ". Back");

                    try {
                        userInput = input.nextLine();
                        userResponse = Integer.parseInt(userInput);
                    } catch (NumberFormatException e) {
                        System.err.println("Must use a valid number to proceed");
                    }

                    if (userResponse < CREATE_SELECTION || userResponse  > BACK){
                        System.err.println("Invalid value. Enter a value: " + CREATE_SELECTION + ", " + REMOVE_SELECTION +
                                ", " + UPDATE_SELECTION + ", " + DISPLAY_SELECTION + ", or " + BACK);
                    }

                    if (userResponse == BACK) {
                        mainSelection();
                    }
                } while (userResponse < CREATE_SELECTION || userResponse  > BACK);

                switch (userResponse) {
                    case CREATE_SELECTION:
                        int dStoreID = getInt("Please enter the store # you want to initiate a delivery: ");
                        ResultSet result = null;
                        String sName = "";
                        String decision;
                        // ID validation starts
                        try {
                            connection = DriverManager.getConnection(URL, USER, PASS);
                            statement = connection.createStatement();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        try {
                            result = statement.executeQuery("SELECT * FROM `stores` WHERE `store_id`=" + dStoreID + ";");
                            if (!result.isBeforeFirst()) {
                                System.err.println(dStoreID+ " is invalid. This ID does not exist.");
                                mainSelection();
                            } while (result.next()) {
                                // Obtain information from SQL search
                                sName = result.getString("store_name");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Do you want to create a delivery for " + sName + "? (yes OR no)");
                        decision = input.nextLine();

                        switch (decision) {
                            case "yes":
                                Boolean dEnroute = null;
                                do {
                                    System.out.println("(true OR false): This delivery is ready for pickup.");
                                    try {
                                        dEnroute = input.nextBoolean();
                                    } catch (InputMismatchException e) {
                                        System.err.println("Please enter only 'true' or 'false'.");
                                        input.next();
                                    }
                                } while (dEnroute == null);
                                input.nextLine();
                                int dQuantity = getInt("Please enter the quantity for this delivery: ");

                                String deliverySql = "INSERT INTO `deliveries`(`store_id`, `refurbished`, `enroute`) " +
                                        "VALUES (?,?,?);";

                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();

                                    preparedStatement = connection.prepareStatement(deliverySql, Statement.RETURN_GENERATED_KEYS);
                                    preparedStatement.setInt(1, dStoreID);
                                    preparedStatement.setInt(2, dQuantity);
                                    preparedStatement.setBoolean(3,dEnroute);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    preparedStatement.executeUpdate();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                // Script to retrieve newly created auto-incremented deliveryID.
                                int deliveryID = -1;
                                try {
                                    result = statement.executeQuery("SELECT LAST_INSERT_ID();");
                                    if (result.next()) {
                                        deliveryID = result.getInt(1);
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Delivery ticket#" + deliveryID + " has been scheduled for store# "+ dStoreID + ".\n");
                                mainSelection();

                            case "no":
                                System.out.println("OK. Returning to menu selection.\n");
                                mainSelection();
                            default:
                                System.err.println("Invalid entry. Returning to menu selection.");
                                mainSelection();
                        }
                        break;

                    case REMOVE_SELECTION:
                        String cancelSql = "DELETE FROM `deliveries` WHERE `delivery_id`=?;";
                        int cancelID = getInt("Please enter the delivery ID# you'd like to cancel:");
                        System.err.println("CONFIRM: You would like to cancel delivery#" + cancelID + "? (yes OR no)");
                        decision = input.nextLine();

                        switch(decision) {
                            case "yes":
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();
                                    result = statement.executeQuery("SELECT * FROM `deliveries` WHERE `delivery_id`=" + cancelID +";");
                                    if(!result.isBeforeFirst()) {
                                        System.err.println("There is no delivery #" + cancelID + " in the database.");
                                        System.out.println("Returning to menu selection.\n");
                                        mainSelection();
                                    } else {
                                        try {
                                            preparedStatement = connection.prepareStatement(cancelSql);
                                            preparedStatement.setInt(1, cancelID);
                                            preparedStatement.executeUpdate();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Delivery #" + cancelID + " has been removed from database.\n");
                                mainSelection();
                            case "no":
                                System.out.println("OK. Returning to menu selection.");
                                mainSelection();
                            default:
                                System.out.println("Invalid entry. Returning to menu selection.\n");
                                mainSelection();
                        }
                    case UPDATE_SELECTION:
                        int editID = getInt("Please enter the delivery ID# you'd like to modify:");
                        System.err.println("CONFIRM: You would like to modify delivery# " + editID + "? (yes OR no)");
                        decision = input.nextLine();

                        switch (decision) {
                            case "yes":
                                String uName = "";
                                Boolean uEnroute = null;
                                // Checks if delivery ID is valid
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();
                                    result = statement.executeQuery("SELECT * FROM `deliveries` WHERE `delivery_id`=" + editID +";");
                                    if (!result.isBeforeFirst()) {
                                        System.err.println("There is no delivery #" + editID + " in the database.");
                                        System.out.println("Returning to menu selection.\n");
                                        mainSelection();
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                do {
                                    System.out.println("(true OR false): This delivery is ready for pickup.");
                                    try {
                                        uEnroute = input.nextBoolean();
                                    } catch (InputMismatchException e) {
                                        System.err.println("Please enter only 'true' or 'false'.");
                                        input.next();
                                    }
                                } while (uEnroute == null);
                                input.nextLine();
                                int uQuantity = getInt("Please update the quantity for delivery#" + editID + ": ");

                                // StoreID editing
                                int uStoreID = getInt("Please update the Store ID# for delivery#" + editID + ": ");
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    result = statement.executeQuery("SELECT * FROM `stores` WHERE `store_id`=" + uStoreID + ";");
                                    if (!result.isBeforeFirst()) {
                                        System.err.println(uStoreID + " is invalid. That Store ID does not exist.");
                                        mainSelection();
                                    } while (result.next()) {
                                        // Obtain information from SQL search
                                        uName = result.getString("store_name");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                System.out.println("Do you want to change delivery# " + editID + " to " + uName + "? (yes OR no)");
                                String userChoice = input.nextLine();
                                switch (userChoice) {
                                    case "yes":
                                        String updateSql = "UPDATE `deliveries` SET `store_id`= ?,`refurbished`= ?,`enroute`= ? WHERE `delivery_id`= " + editID +";";
                                        try {
                                            connection = DriverManager.getConnection(URL, USER, PASS);
                                            statement = connection.createStatement();

                                            preparedStatement = connection.prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS);
                                            preparedStatement.setInt(1, uStoreID);
                                            preparedStatement.setInt(2, uQuantity);
                                            preparedStatement.setBoolean(3, uEnroute);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            preparedStatement.executeUpdate();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        System.out.println("Delivery# " + editID + " has been updated.\n");
                                        mainSelection();
                                    case "no":
                                        System.out.println("OK. Returning to menu selection.\n");
                                        mainSelection();
                                    default:
                                        System.out.println("Invalid entry. Returning to menu selection.\n");
                                        mainSelection();
                                }
                            case "no":
                                System.out.println("OK. Returning to menu selection.\n");
                                mainSelection();
                            default:
                                System.out.println("Invalid entry. Returning to menu selection.\n");
                                mainSelection();
                        }
                    case DISPLAY_SELECTION:
                        int displayID = getInt("Please enter the delivery# you'd like to view: ");
                        System.out.println("Are you sure you want to view Delivery#" + displayID + "? (yes OR no)");
                        decision = input.nextLine();

                        switch (decision) {
                            case "yes":
                                try {
                                    connection = DriverManager.getConnection(URL, USER, PASS);
                                    statement = connection.createStatement();

                                    result = statement.executeQuery("SELECT * FROM `deliveries` WHERE delivery_id =  "
                                            + displayID + ";");
                                    if (!result.isBeforeFirst()) {
                                        System.err.println(displayID + " is invalid. This ID does not exist.\n");
                                    } while (result.next()) {
                                        System.out.print("\nID: " + result.getInt("delivery_id"));
                                        System.out.print("\nName: " + result.getTimestamp("delivery_date"));
                                        System.out.print("\nStore #: " + result.getInt("store_id"));
                                        System.out.print("\nDelivery QTY: " + result.getInt("refurbished"));
                                        System.out.print("\nCurrently enroute?: " + result.getBoolean("enroute"));
                                        System.out.println("\n----");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            case "no":
                                System.out.println("OK. Returning to menu selection.\n");
                                mainSelection();
                            default:
                                System.out.println("Invalid entry. Returning to menu selection.\n");
                                mainSelection();
                        }
                    case BACK:
                        mainSelection();
                }
            } // end of delivery menu

        } while (userResponse < CREATE_SELECTION || userResponse  > EXIT);

        return userResponse;

    } // End of main menu selection

} // End of main