package Classes;

// Javafx import statements needed to run.
import javafx.scene.control.Alert;
import javafx.scene.text.Text;

// Java/MySQL import statements needed to run.
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is for handling the User database and scheduler functionality.
 * This class accesses the database through the database static class.
 * @author Dakota Ray Tominus
 */
public class UserHelper extends Helper {

    /**
     * String representation of the class type
     */
    String string = "User Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public UserHelper(){

        super();
    }

    /**
     * This method is used to process the exception adn prompt the user to seek help.
     * @param thread The thread the exception was thrown on
     * @param throwable The exception that was thrown
     */
    @Override
    public void throwException(Thread thread, Throwable throwable){

        JOptionPane.showMessageDialog(null, "Caught Unchecked exception in " + this + ". Please contact IT.");
    }


    /**
     * This method is for getting the string representation of this class
     * @return the string representation for the class
     */
    @Override
    public String toString(){

        return string;
    }

    /**
     * This method is used to check the username against the password in the database.
     * Returns true if the username matched the password.
     * Checks for null or empty fields and displays errors.
     * Alerts user if password does not match username.
     * @param userId The username of the user trying to log in.
     * @param userIDError The username error fields for resulting errors.
     * @param password The users entered password.
     * @param userPasswordError The user password error field for password and log in related errors.
     * @return A Boolean representation of whether the username and password match within the database.
     */
    public static boolean checkUserNameToPassword(String userId, Text userIDError, String password, Text userPasswordError) {

        // Checks if the user id field is null or empty.
        if (userId == null || userId.trim().length() == 0){

            // Sets up and error message if found to be empty or null.
            userIDError.setText(LanguageHelper.getResourceBundle().getString("text.login.error.noUserName"));
        }

        // Checks if the user password field is null or empty.
        if (password == null || password.trim().length() == 0){

            // Sets up and error message if found to be empty or null.
            userPasswordError.setText(LanguageHelper.getResourceBundle().getString("text.login.error.noPassword"));
        }

        // Packages the strings for use in the database static class.
        String[] strings = {userId, password};

        // Try to catch the SQLException
        try{

            // Uses the provided SQL Query to get a result set from the database checking if the provided password matches for the provided username.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT User_Name " +
                    "FROM users " +
                    "WHERE (User_Name = ? AND Password = ?)", strings);

            // Checks if the result set has a result.
            if(result.next()){

                // If there is a result, user was validated, set the logged-in user.
                Database.getInstance().setLoggedInUser(userId);

                // Returns true.
                return true;
            }

            // If not set Error message showing failed login.
            userPasswordError.setText(LanguageHelper.getResourceBundle().getString("text.login.error.failedLogin"));
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Returns false if password match fails.
        return false;
    }

    /**
     * This method is used to get all the users from the database using the provided SQL Query.
     * Returns the result set of all users in the database.
     * @return Result set of all the users in the database.
     */
    public static ResultSet getAllUsers() {

        // Return the result set of all users in the database.
        return Database.getInstance().getResults("SELECT User_id " +
                "FROM users");
    }

    /**
     * This method is used to get the user ID from the database using the username.
     * Returns the user ID for the provided username.
     * @param loggedInUser The logged-in users' username.
     * @return The user ID for the provided username.
     */
    private static String getUserIdFromName(String loggedInUser) {

        // Packages the strings for use in the database static class.
        String[] strings = {loggedInUser};

        // Try to catch the SQL Exception.
        try{

            // Uses the provided SQL Query to get a result set from the database retrieving the user ID associated with the username.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT User_ID " +
                    "FROM users " +
                    "WHERE User_Name = ?", strings);

            // Checks if the result set has a result.
            if(result.next()){

                // If the result set has a result, return the User ID.
                return result.getString("User_ID");
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Returns null if it fails.
        return null;
    }

    /**
     * This method is used to get the user ID from the provided appointment.
     * Returns User ID from the appointment ID provided
     * @param appointmentID Appointment whose users ID is needed.
     * @return The User ID associated with the provided appointment ID.
     */
    public static String getUserIDFromAppointment(String appointmentID) {

        // Packages the strings for use in the database static class.
        String[] strings = {appointmentID};

        // Try to catch the SQL Exception.
        try{

            // Uses the provided SQL Query to get a result set from the database retrieving the user ID associated with the appointment ID.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT User_ID " +
                    "FROM appointments " +
                    "WHERE Appointment_ID = ?", strings);

            // Checks if the result set has a result.
            if(result.next()){

                // If the result set has a result, return the User ID.
                return String.valueOf(result.getInt("User_ID"));
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Returns null if it fails.
        return null;
    }

    /**
     * This method is used to check if there is an appointment between now and the provided time distance.
     * Returns true if an appointment was found.
     * @param timeToCheck The time to check if there is an appointment within the time frame.
     * @return The boolean representation of whether an appointment was found.
     */
    public static boolean hasAppointmentWithin(int timeToCheck) {

        // Packages the strings for use in the database static class.
        String[] strings = {getUserIdFromName(Database.getInstance().getLoggedInUser()), String.valueOf(timeToCheck)};

        // Try to catch the SQL Exception.
        try{

            // Uses the provided SQL Query to get a result set from the database checking if there is an appointment within a time frame.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT COUNT(*) AS Count " +
                    "FROM appointments " +
                    "WHERE User_ID = ? AND Start <= now() + INTERVAL ? MINUTE AND Start >= now()", strings);

            // Checks if the result set has a result.
            if (result.next() ) {

                // Gets the result to test.
                int test = result.getInt("Count");

                // Checks if the test is not 0.
                if (test != 0) {

                    // If not 0, return true.
                    return true;
                }
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Returns false if it fails.
        return false;
    }

    /**
     * This method is used to set up alerts for users when they log in.
     * Checks the database for upcoming appointment.
     * @param alert The alert to be populated with data from the database.
     */
    public static void setUpAlertForUserNextAppointment(Alert alert) {

        // Packages the strings for use in the database static class.
        String[] strings = {getUserIdFromName(Database.getInstance().getLoggedInUser())};

        // Try to catch the SQL Exception.
        try {

            // Uses the provided SQL Query to get a result set from the database regarding the next appointment.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT Appointment_ID, appointments.Start " +
                    "FROM appointments " +
                    "WHERE User_ID = ? AND Start >= now() " +
                    "ORDER BY Start " +
                    "Limit 1", strings);

            // Checks if the result set has a result.
            if(result.next()){

                // If there is a result, store the appointment ID.
                String appointmentID = result.getString("appointment_ID");

                // Store the Start.
                String startDateTime = result.getString("Start");

                // Set the provided alerts content using the stored strings.
                alert.setContentText("#" + appointmentID + " @ " + startDateTime);
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }
    }
}
