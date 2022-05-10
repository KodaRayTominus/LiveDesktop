package Classes;

// Java/MySQL import statements needed to run.
import java.sql.*;


/**
 * This class is for handling the database functionality.
 * This class accesses the database through Java/SQL methods and receiving the SQL and values as parameters.
 * @author Dakota Ray Tominus
 */
public class Database {

    // Static database field for holding the programs' database instance.
    private final static Database database = new Database();

    // The current logged-in user.
    private static String loggedInUser;

    // The List for the types of appointments possible.
    private static String[] appointmentTypes = {"Consultation", "De-Briefing", "On-Boarding", "Planning Session", "Signing"};

    // URL for the database access.
    String url = "jdbc:mysql://localhost:3306/client_schedule?useSSL=false";

    // Username for the database access.
    String userName = "sqlUser";

    // Password for the database access.
    String password = "Passw0rd!";

    // The connection to the database.
    Connection connection;

    /**
     * This method is used as a placeholder to instantiate the database instance.
     */
    private Database(){

    }

    /**
     * This method is used to get the instance of the database.
     * @return The instance of the database class.
     */
    public static Database getInstance(){

        // Return the instance of the database class.
        return database;
    }

    /**
     * This method is used to start the connection to the database.
     */
    public void startConnection(){

        // Try to catch the SQL Exception.
        try{

            // Sets up the connection.
            connection = DriverManager.getConnection(
                    url,
                    userName,
                    password
            );
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to get a result set.
     * @param sql The SQL Query to be used for getting the desired result set.
     * @return The desired result set, retrieved using the provided SQL Query.
     */
    public ResultSet getResults(String sql) {

        // Sets up the result set for later use.
        ResultSet results = null;

        // Try to catch the SQL Exception.
        try{

            // Prepares the SQL Query.
            PreparedStatement query = connection.prepareStatement(sql);

            // Executes the query.
            results = query.executeQuery();
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Returns the results.
        return results;
    }

    /**
     * This method is used to get a result set using strings.
     * @param sql The SQL Query to be used for getting the desired result set.
     * @param strings The Strings to be used for getting the desired result set.
     * @return The desired result set, retrieved using the provided SQL Query and Strings.
     */
    public ResultSet getResultsUsingStrings(String sql, String[] strings) {

        // Sets up the result set for later use.
        ResultSet results = null;

        // Try to catch the SQL Exception.
        try{

            // Prepares the SQL Query.
            PreparedStatement query = connection.prepareStatement(sql);

            // For each string in the set of strings.
            for (int i = 0; i < strings.length; i++) {

                // Set the string in the SQL Query.
                query.setString(i + 1, strings[i]);
            }

            // Executes the query.
            results = query.executeQuery();
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Returns the results.
        return results;
    }

    /**
     * This method is used to update a data set using strings.
     * @param sql The SQL Query to be used for getting the desired result set.
     * @param strings The Strings to be used for getting the desired result set.
     * @return The int representation of how many rows were effected.
     */
    public int UpdateUsingStrings(String sql, String[] strings) {

        // Sets up the results for later use.
        int results = 0;

        // Try to catch the SQL Exception.
        try{

            // Prepares the SQL Query.
            PreparedStatement query = connection.prepareStatement(sql);

            // For each string in the set of strings.
            for (int i = 0; i < strings.length; i++) {

                // Set the string in the SQL Query.
                query.setString(i + 1, strings[i]);
            }

            // Executes the update query.
            results = query.executeUpdate();
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Returns the results.
        return results;
    }

    /**
     * This method is used to get the current logged-in user.
     * @return The string representation of the current logged-in user.
     */
    public String getLoggedInUser() {

        // Return the current logged-in user.
        return loggedInUser;
    }

    /**
     * This method is used to set the current logged-in user.
     * @param id The ID of the current logged-in user.
     */
    public void setLoggedInUser(String id) {

        // Sets the current logged-in user ID.
        loggedInUser = id;
    }

    /**
     * This method is used to get all the appointment types.
     * @return The array of appointment types.
     */
    public String[] getAppointmentTypes(){

        // Return the appointment types.
        return appointmentTypes;
    }

    /**
     * This method is used to set all the appointment types.
     * @param appointmentTypes The appointment types to be set.
     */
    public void setAppointmentTypes(String[] appointmentTypes){

        // Sets the appointment types array.
        Database.appointmentTypes = appointmentTypes;
    }
}
