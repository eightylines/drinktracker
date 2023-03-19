# Energy Drink App

This is a simple Java console application for managing an energy drink inventory.

## Features

- Create a new energy drink
- Update an existing energy drink
- Remove an energy drink
- Display all energy drinks

## Prerequisites

- JDK 8 or higher
- MySQL Server

## Setup

1. Make sure you have a MySQL server running and accessible.
2. Create a new database and import the `EnergyDrinksDB.sql` file (if provided) or run the SQL commands from the Java application to create the `EnergyDrinks` table.
3. Update the `DB_URL`, `USER`, and `PASS` constants in the `EnergyDrinkApp.java` file with the appropriate values for your MySQL server.

## Running the Application

1. Open a terminal or command prompt and navigate to the project folder.
2. Compile the Java application with the following command: javac EnergyDrinkApp.java
3. Run the compiled Java application with the following command: java EnergyDrinkApp
4. Follow the on-screen prompts to use the application.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

