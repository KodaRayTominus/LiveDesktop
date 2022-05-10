package Classes;

// Java/MySQL import statements needed to run.
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is for handling the Country database and scheduler functionality.
 * This class accesses the database through the database static class.
 * @author Dakota Ray Tominus
 */
public class CountryHelper extends Helper {

    /**
     * String representation of the class type
     */
    String string = "Country Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public CountryHelper(){

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
     * This method is used to get all the countries from the database using the provided SQL Query.
     * Returns the result set of all countries in the database.
     * @return Result set of all the countries in the database.
     */
    public static ResultSet getAllCountries() {

        // Return the result set of all countries in the database.
        return Database.getInstance().getResults("SELECT Country " +
                "FROM countries");
    }

    /**
     * This method is used to get a country ID, with the matching country name, using the provided SQL Query.
     * Returns the string ID matching the country name provided.
     * @param countryName The countries name whose ID is needed.
     * @return The String ID of the country requested.
     */
    public static String getCountryID(String countryName){

        // Sets up the results for later use.
        ResultSet results;

        // Packages the strings for use in the database static class.
        String[] strings = {countryName};

        // Try to catch the SQL Exception.
        try{

            // Get the results set for the country ID requested.
            results = Database.getInstance().getResultsUsingStrings("SELECT Country_ID " +
                    "FROM countries " +
                    "WHERE Country = ?", strings);

            // Checks if the result set has a result.
            if(results.next()){

                // If it has results, return the country ID.
                return results.getString("Country_ID");
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If it fails, return null.
        return null;
    }
}
