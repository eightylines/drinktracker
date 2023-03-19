package edu.seminolestate.cis2901.drinktracker;
import java.sql.*;
import java.time.*;
import java.util.*;

public class SqlConnect {

	// Login information
	static final String DATABASE_URL = "jdbc:mysql://localhost:3306/drinktracker";
	static final String USER = "root";
	static final String PASS = "password";

	// Initialize a scanner and menu choices for user input here:
	private static final Scanner input = new Scanner(System.in);
	private static final int CREATE_SELECTION = 1;
	private static final int REMOVE_SELECTION = 2;
	private static final int UPDATE_SELECTION = 3;
	private static final int DISPLAY_SELECTION = 4;
	// Added BACK for future increments
	private static final int EXIT = 5;
	private static final int BACK = 6;


	public static void main(String[] args) throws SQLException {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		// Using a try/catch for SQL connection exceptions
		try {
			// Initializing connection:
			connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);

		} catch (SQLException e) {
			// This will handle JDBC errors
			e.printStackTrace();
		} catch (Exception e) {
			// This will handle other errors
			e.printStackTrace();
		} // End of try

		System.out.println("=====================================");
		System.out.println("========= DRINK TRACKER 1.3 =========");
		System.out.println("=====================================\n");

		// Main menu selection
		mainSelection();

		// End of main menu selection

		// Drink Selection
		int response = 0;
		do {
			response = drinkSelection(1);

			switch (response) {
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

					/* 3. Set values with user input */
					try {
						preparedStatement = connection.prepareStatement(createSql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						preparedStatement.setInt(1, tempID);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						preparedStatement.setString(2, tempName);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						preparedStatement.setString(3, tempDesc);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						preparedStatement.setBoolean(4, tempAvail);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						preparedStatement.setTimestamp(5, tempStamp);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						preparedStatement.setInt(6, tempStock);
					} catch (SQLException e) {
						e.printStackTrace();
					}

					/* 4. Submits and executes the query */
					try {
						preparedStatement.executeUpdate();
						System.out.println("Product " + tempID + " has been created on the database.\n");
					} catch (SQLIntegrityConstraintViolationException e) {
						System.err.println("Please try again with different 'Product ID'. " + tempID + " already exists.\n");
					} catch (SQLException e) {
						e.printStackTrace();
					}
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
								Connection connection1 = DriverManager.getConnection(DATABASE_URL, USER, PASS);
								Statement stmt = connection1.createStatement();
								ResultSet result = null;

								try {
									result = stmt.executeQuery("SELECT * FROM `products` WHERE `product_id`=" + tempRemove +";");
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
								Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
								Statement stmt = conn.createStatement();
								ResultSet result = null;

								try {
									result = stmt.executeQuery("SELECT * FROM `products` WHERE `product_id`=" + updateID + ";");
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
										} catch (SQLException e) {
											e.printStackTrace();
										}

										try {
											preparedStatement.setString(1, updateName);
										} catch (SQLException e) {
											e.printStackTrace();
										}

										try {
											preparedStatement.setString(2, updateDesc);
										} catch (SQLException e) {
											e.printStackTrace();
										}

										try {
											preparedStatement.setBoolean(3, updateAvail);
										} catch (SQLException e) {
											e.printStackTrace();
										}

										try {
											preparedStatement.setTimestamp(4, updateStamp);
										} catch (SQLException e) {
											e.printStackTrace();
										}

										try {
											preparedStatement.setInt(5, updateStock);
										} catch (SQLException e) {
											e.printStackTrace();
										}

										try {
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
								System.out.println("Returning to menu selection.\n");
								break;
						}
						break;
					}
					break;

				case DISPLAY_SELECTION:
					String selectDecision;
					boolean selectChoice = true;

					while (selectChoice) {
						int selectID = getInt("Enter the Product ID for the drink you want to view: ");
						System.out.println("Are you sure you want to view " + selectID + "? (yes OR no)");
						selectDecision = input.nextLine();

						switch (selectDecision) {
							case "yes":
								// ID validation
								Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
								Statement stmt = conn.createStatement();
								String query = "SELECT `product_id`, `product_name`, " +
										"`description`, `product_available`, `product_moved`, `product_stock` " +
										"FROM `products` WHERE `product_id`=" + selectID + ";";
								ResultSet result = stmt.executeQuery(query);

								try {
									result = stmt.executeQuery("SELECT * FROM `products` WHERE `product_id` = "
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
					break;

				case BACK:
					/* Returns to main menu selection
					 */
					mainSelection();
					break;


			}

		// Store Selection
		// TODO: Add the CRUD for store
		response = 0;
		switch(response) {
			case CREATE_SELECTION:
				int tempID = getInt("Enter the Store ID: ");
				String tempName = getString("Enter Store's Name: ");
				// int store_id, string store_name, timestamp last_updated, int store_stock, boolean restocked
				Timestamp timeUpdated = Timestamp.from(Instant.now());
				int tempStock = getInt("Enter the store's initial drink quantity stock: ");
				Boolean restocked = null;
				do {
					System.out.println("(true OR false): This store requires nightly restocking?");
					try {
						restocked = input.nextBoolean();
					} catch (InputMismatchException e) {
						System.err.println("Please enter only 'true' OR 'false'.");
						input.next();
					}
				} while (restocked == null);
				input.nextLine(); // Flushes scanner

				/* Creates SQL statement and sets values */
				String addStore = "INSERT INTO `stores`(`store_id`, `store_name`, `last_updated`, `store_stock`, `restocked`)" +
						" VALUES (?,?,?,?,?)";

				try {
					preparedStatement = connection.prepareStatement(addStore);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					preparedStatement.setInt(1, tempID);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setString(2, tempName);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setTimestamp(3, timeUpdated);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setInt(4, tempStock);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setBoolean(5, restocked);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				/* Submits the query */
				try {
					preparedStatement.executeUpdate();
					System.out.println(tempName + " has been created on the database.\n");
				} catch (SQLIntegrityConstraintViolationException e) {
					System.err.println("Please try again with a different Store ID. " + tempID + " already exists.\n");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;

			case REMOVE_SELECTION:
			case UPDATE_SELECTION:
			case DISPLAY_SELECTION:
			case BACK:
				break;
		}

		} while (response != BACK);
		

		input.close(); // Closes scanner

	} // End of main

	// Creates drink menu
	private static int drinkSelection(int response) {
		int userResponse = 0;
		String userInput;

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

		return userResponse;
		
	} // End of drink menu

	// Creates the store menu selection
	private static int storeSelection() {
		int userResponse = 0;
		String userInput;

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

			// Will send user to corresponding option
			if (userResponse == CREATE_SELECTION) {
				storeSelection();
			}

			if (userResponse == REMOVE_SELECTION) {
				storeSelection();
			}

			if (userResponse == UPDATE_SELECTION) {
				System.err.println("Option is unavailable");
				mainSelection();
			}

			if (userResponse == DISPLAY_SELECTION) {
				System.err.println("Option is unavailable");
				mainSelection();
			}

		} while (userResponse < CREATE_SELECTION || userResponse  > BACK);

		return userResponse;

	} // End of store menu

	// Creates a main menu selection
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

			// Will send user to corresponding option
			if (userResponse == CREATE_SELECTION) {
				drinkSelection(0);
			}

			if (userResponse == REMOVE_SELECTION) {
				storeSelection();
			}

			if (userResponse == UPDATE_SELECTION) {
				System.err.println("Option is unavailable");
				mainSelection();
			}

			if (userResponse == DISPLAY_SELECTION) {
				System.err.println("Option is unavailable");
				mainSelection();
			}

		} while (userResponse < CREATE_SELECTION || userResponse  > EXIT);

		return userResponse;

	} // End of main menu selection

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
			}
		} while (userValue == null || userValue.length() < 1 || userValue.length() > 50);

		return userValue;
	} // End of GetString
}// End of SqlConnect
