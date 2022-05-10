package Classes;

// Java/MySQL import statements needed to run.
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is for handling the Contact database and scheduler functionality.
 * This class accesses the database through the database static class.
 * @author Dakota Ray Tominus
 */
public class ContactHelper extends Helper {

    /**
     * String representation of the class type
     */
    String string = "Contact Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public ContactHelper(){

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
     * This method is used to get all the contacts from the database using the provided SQL Query.
     * Returns the result set of all contacts in the database.
     * @return Result set of all the contacts in the database.
     */
    public static ResultSet getAllContacts() {

        // Return the result set of all contacts in the database.
        return Database.getInstance().getResults("SELECT Contact_Name " +
                "FROM contacts");
    }

    /**
     * This method is used to get the contact ID matching the name given, using the provided SQL Query.
     * Returns the string ID of the contact requested.
     * @param selectedContact The contacts name that's ID is being requested.
     * @return The contact ID of the provided contact.
     */
    public static String getContactID(String selectedContact) {

        // Packages the strings for use in the database static class.
        String[] strings = {selectedContact};

        // Try to catch the SQL Exception.
        try{

            // Get the result set using the provided SQL Query.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT Contact_ID " +
                    "FROM contacts " +
                    "WHERE Contact_Name = ?", strings);

            // Checks if the result set has a result.
            if(result.next()){

                // If result exists, return the contact ID.
                return String.valueOf(result.getInt("Contact_ID"));
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If all fails return null.
        return null;
    }
}
