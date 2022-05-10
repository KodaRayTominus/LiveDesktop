package Classes;

// Java/MySQL import statements needed to run.
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is for handling the Customer database and scheduler functionality.
 * This class accesses the database through the database static class.
 * @author Dakota Ray Tominus
 */
public class CustomerHelper  extends Helper {

    /**
     * String representation of the class type
     */
    String string = "Customer Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public CustomerHelper(){

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
     * This method is used to get all the customers from the database using the provided SQL Query.
     * Returns the result set of all customers in the database.
     * @return Result set of all the customers in the database.
     */
    public static ResultSet getAllCustomers() {

        // Return the result set of all the customers in the database.
        return Database.getInstance().getResults("SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, " +
                "first_level_divisions.Division, countries.Country, customers.Postal_Code, customers.Phone, customers.Last_Update " +
                "FROM customers " +
                "INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID " +
                "INNER JOIN countries ON first_level_divisions.COUNTRY_ID = countries.Country_ID");
    }

    /**
     * This method is used to save the customer data to the database, using the provided SQL Query and User provided data.
     * Checks if the customer exists.
     * Will update if customer exists, and add a new one if not.
     * Returns true if the operation was a success.
     * @param id The id used to check if the customer exists or not.
     * @param name The name of the customer to be saved to the database.
     * @param address The address of the customer to be saved to the database.
     * @param postalCode The postal code of the customer to be saved to the database.
     * @param phone The phone number of the customer to be saved to the database.
     * @param division The division of the customer to be saved to the database.
     * @return The boolean representation of whether the addition or update was successful.
     */
    public static boolean saveCustomerData(String id, String name, String address, String postalCode, String phone, String division){

        // Set up the result for later use.
        int result;

        // Check if the customer exists.
        if(customerExists(id)){

            // If it exists, packages the strings for use in the database static class.
            String[] strings = {name, address, postalCode, phone, Database.getInstance().getLoggedInUser(), division, id};

            // Gets the numeric representation of whether the customer was successfully added to the database.
            result = Database.getInstance().UpdateUsingStrings("UPDATE customers SET Customer_Name = ?, Address = ?, " +
                    "Postal_Code = ?, Phone = ?, Last_Update = CONVERT_TZ(now(), @@SESSION.time_zone, '+00:00'), " +
                    "Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?", strings);
        }
        else{

            // If it does not exist, package the strings for use in the database static class.
            String[] strings = {name, address, postalCode, phone, Database.getInstance().getLoggedInUser(),
                    Database.getInstance().getLoggedInUser(), division};

            // Gets the numeric representation of whether the customer was updated in the database.
            result = Database.getInstance().UpdateUsingStrings("INSERT INTO customers(Customer_Name, Address, " +
                    "Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                    "VALUES(?, ?, ?, ?, CONVERT_TZ(now(), @@SESSION.time_zone, '+00:00'), ?, " +
                    "CONVERT_TZ(now(), @@SESSION.time_zone, '+00:00'), ?, ?)", strings);
        }

        // Return true if result is equal to one.
        return result == 1;
    }

    /**
     * This method is used to check if the customer exists in the database with the name and ID provided.
     * @param id The Customers ID to be used to check is the customer exists.
     * @return the boolean representation of whether the customer exists.
     */
    private static boolean customerExists(String id) {

        // Packages the strings for use in the database static class.
        String[] strings = {id};

        // Try to catch the SQL Exception.
        try {

            // Gets the numeric representation of whether the customer was found in the database.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT Customer_ID " +
                    "FROM customers " +
                    "WHERE Customer_ID = ?", strings);

            // Checks if the result set has a result.
            if(result.next()){

                // If there is a result, match found, return true.
                return true;
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If no match found, customer does  not exist, return false.
        return false;
    }

    /**
     * This method is used to generate the next customer ID using the provided SQL Query.
     * This ID is not final and may be changed at injection.
     * @return The Next hypothetical string Customer ID.
     */
    public static String generateNextCustomerId() {

        // Try to catch the SQL Exception.
        try{

            // Get the next hypothetical ID using the auto increment from the database.
            ResultSet result = Database.getInstance().getResults("SELECT AUTO_INCREMENT " +
                    "FROM information_schema.tables " +
                    "WHERE table_name='customers'");

            // Checks if the result set has a result.
            if(result.next()){

                // If there is a result, return the next auto incremented ID from the database.
                return String.valueOf(result.getInt("AUTO_INCREMENT"));
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If fails return null.
        return null;
    }

    /**
     * This method is used to delete a customer, using the provided SQL Query.
     * Returns a boolean representation of whether the deletion was successful.
     * @param customer_id The customer ID of the customer that will be deleted.
     * @return The boolean representation of whether the customer was deleted.
     */
    public static boolean deleteCustomer(String customer_id) {

        // Sets up result for later use.
        int result;

        // Packages the strings for use in the database static class.
        String[] strings = {customer_id};

        // Get the numeric representation of whether the customer was deleted.
        result = Database.getInstance().UpdateUsingStrings("DELETE FROM customers " +
                "WHERE customer_id = ?", strings);

        // return true is result is equal to one.
        return result == 1;
    }

    /**
     * This method gets and returns the search results for the given string.
     * @return The results of the search.
     */
    public static ResultSet getSearchResults(String newVal) {

        return Database.getInstance().getResults("SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, " +
                "first_level_divisions.Division, countries.Country, customers.Postal_Code, customers.Phone, customers.Last_Update " +
                "FROM customers " +
                "INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID " +
                "INNER JOIN countries ON first_level_divisions.COUNTRY_ID = countries.Country_ID " +
                "WHERE Customer_ID LIKE '%" + newVal + "%' OR Customer_Name LIKE '%" + newVal + "%' OR Address LIKE '%" + newVal +
                "%' OR Division LIKE '%" + newVal + "%' OR Country LIKE '%" + newVal + "%' OR Postal_Code LIKE '%" + newVal +
                "%' OR Phone LIKE '%" + newVal + "%'");
    }
}
