package Classes;

// Java/MySQL import statements needed to run.
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is for handling the First level division database and scheduler functionality.
 * This class accesses the database through the database static class.
 * @author Dakota Ray Tominus
 */
public class FirstLevelDivisionHelper extends Helper {

    /**
     * String representation of the class type
     */
    String string = "First Level Division Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public FirstLevelDivisionHelper(){

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
     * This method is used to get all first level divisions from the database associated with a country, matching the provided country ID, using the provided SQL Query.
     * Returns the result set of all first level divisions from the database associated with a country in the database.
     * @param countryID The country ID for the country whose divisions are needed.
     * @return Result set of all the first level divisions from the database associated with a country in the database.
     */
    public static ResultSet getAllFirstLevelDivisions(String countryID) {

        // Packages the strings for use in the database static class.
        String[] strings = {countryID};

        // Return the result set of all associated divisions in the database.
        return Database.getInstance().getResultsUsingStrings("SELECT Division " +
                "FROM first_level_divisions " +
                "WHERE COUNTRY_ID = ?", strings);
    }

    /**
     * This method is used to retrieve a division ID from the database, matching the divisions name, using the provided SQL Query.
     * Returns a string ID for the division whose name was provided.
     * @param selectedDivision The division name of the division in which the ID is needed.
     * @return The String ID for the division that was provided.
     */
    public static String getDivisionID(String selectedDivision) {

        // Packages the strings for use in the database static class.
        String[] strings = {selectedDivision};

        // Try to catch the SQL Exception.
        try{

            // generate/get new customer id
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT Division_ID " +
                    "FROM first_level_divisions " +
                    "WHERE Division = ?", strings);

            // Checks if the result set has a result.
            if(result.next()){

                // If it has a result, return the Division ID.
                return String.valueOf(result.getInt("Division_ID"));
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If all fails, return null.
        return null;
    }
}
