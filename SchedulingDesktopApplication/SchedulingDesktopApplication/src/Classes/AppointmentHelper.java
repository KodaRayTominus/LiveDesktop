package Classes;

// Javafx import statements needed to run.
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;

// Java import statements needed to run.
import javax.swing.*;
import java.time.*;

// Java/MySQL import statements needed to run.
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is for handling the Appointment database and scheduler functionality.
 * This class accesses the database through the database static class.
 * @author Dakota Ray Tominus
 */
public class AppointmentHelper extends Helper {

    /**
     * String representation of the class type
     */
    String string = "Appointment Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public AppointmentHelper(){

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
     * This method is used to get all the appointments from the database using the provided SQL Query.
     * Returns the result set of all appointments in the database.
     * @return Result set of all the appointments in the database.
     */
    public static ResultSet getAllAppointments() {

        // Return the result set of all appointments in the database.
        return Database.getInstance().getResults("SELECT appointments.Appointment_ID, appointments.Title, " +
                "appointments.Description, appointments.Location, contacts.Contact_Name, appointments.Type, " +
                "appointments.Start, appointments.End, appointments.Customer_ID " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID");
    }

    /**
     * This method is used to get all the months appointments from the database using the provided SQL Query.
     * Returns the result set of all the months appointments in the database.
     * @return Result set of all the months appointments in the database.
     */
    public static ResultSet getMonthsAppointments() {

        // Return the result set of all the months appointments in the database.
        return Database.getInstance().getResults("SELECT appointments.Appointment_ID, appointments.Title, " +
                "appointments.Description, appointments.Location, contacts.Contact_Name, appointments.Type, " +
                "appointments.Start, appointments.End, appointments.Customer_ID," +
                "MONTH(Start) AS Month " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID " +
                "WHERE MONTH(Start) = MONTH(now())");
    }

    /**
     * This method is used to get all the appointments within the next week from the database using the provided SQL Query.
     * Returns the result set of all appointments within the next week in the database.
     * @return Result set of all the appointments within the next week in the database.
     */
    public static ResultSet getWeeksAppointments() {

        // Return the result set of all appointments within the next week in the database.
        return Database.getInstance().getResults("SELECT appointments.Appointment_ID, appointments.Title, " +
                "appointments.Description, appointments.Location, contacts.Contact_Name, appointments.Type, " +
                "appointments.Start, appointments.End, appointments.Customer_ID, " +
                "MONTH(Start) AS Month, DAY(Start) AS Day " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID "  +
                "WHERE MONTH(Start) = MONTH(now()) AND " +
                "DAY(Start) < DAY(now()) + 7 AND " +
                "DAY(Start) > DAY(now()) - 1");
    }

    /**
     * This method is used to save the appointment data to the database, using the provided SQL Query and User provided data.
     * Checks if the appointment exists.
     * Will update if appointment exists, and add a new one if not.
     * Returns true if the operation was a success.
     * @param id The id used to check if the customer exists or not.
     * @param title The title of the appointment to be saved to the database.
     * @param description The description of the appointment to be saved to the database.
     * @param location The location of the appointment to be saved to the database.
     * @param contact The contact of the appointment to be saved to the database.
     * @param type The type of the appointment to be saved to the database.
     * @param start The start datetime of the appointment to be saved to the database.
     * @param end The end datetime of the appointment to be saved to the database.
     * @param zoneOffSet The zone offset of the user to be used when saving a datetime to the database.
     * @param customerID The customer ID of the appointment to be saved to the database.
     * @param userID The title of the appointment to be saved to the database.
     * @return The boolean representation of whether the operation was successful.
     */
    public static boolean saveAppointmentData(String id, String title, String description, String location, String contact,
                                              String type, String start, String end, String zoneOffSet, String customerID, String userID) {

        // Sets up the result for later use.
        int result;

        // Checks if the appointment exists.
        if(appointmentExists(id)){

            // If it exists, packages the strings for use in the database static class.
            String[] strings = {title, description, location, type, start, zoneOffSet, end, zoneOffSet,
                    Database.getInstance().getLoggedInUser(), customerID, userID, ContactHelper.getContactID(contact), id};

            // Gets the numeric result of whether the appointment was successfully added to the database.
            result = Database.getInstance().UpdateUsingStrings("UPDATE appointments SET Title = ?, " +
                    "Description = ?, Location = ?, Type = ?, Start = CONVERT_TZ(?, ?, '+00:00'), " +
                    "End = CONVERT_TZ(?, ?, '+00:00'), Last_Update = CONVERT_TZ(now(), @@SESSION.time_zone, '+00:00'), " +
                    "Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?", strings);
        }
        else{

            // If it does not exist, package the strings for use in the database static class.
            String[] strings = {title, description, location, type, start, zoneOffSet, end, zoneOffSet,
                    Database.getInstance().getLoggedInUser(), Database.getInstance().getLoggedInUser(),
                    customerID, userID, ContactHelper.getContactID(contact)};


            // Gets the numeric result of whether the appointment was updated in the database.
            result = Database.getInstance().UpdateUsingStrings("INSERT INTO appointments(Title, Description, " +
                    "Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, " +
                    "Customer_ID, User_ID, Contact_ID) VALUES(?, ?, ?, ?, CONVERT_TZ(?, ?, '+00:00'), " +
                    "CONVERT_TZ(?, ?, '+00:00'), CONVERT_TZ(now(), @@SESSION.time_zone, '+00:00'), ?, " +
                    "CONVERT_TZ(now(), @@SESSION.time_zone, '+00:00'), ?, ?, ?, ?)", strings);
        }

        // Return true if result is equal to one.
        return result == 1;
    }

    /**
     * This method is used to generate the next appointment ID using the provided SQL Query.
     * This ID is not final and may be changed at injection.
     * @return The Next hypothetical string appointment ID.
     */
    public static String generateNextAppointmentId() {

        // Try to catch the SQL Exception.
        try{

            // Get the next hypothetical ID using the auto increment from the database.
            ResultSet result = Database.getInstance().getResults("SELECT AUTO_INCREMENT " +
                    "FROM information_schema.tables " +
                    "WHERE table_name='appointments'");

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
     * This method is used to check if the appointment exists in the database with the name and ID provided.
     * @param id The Appointment ID to be used to check is the appointment exists.
     * @return the boolean representation of whether the appointment exists.
     */
    private static boolean appointmentExists(String id) {

        // Packages the strings for use in the database static class.
        String[] strings = {id};

        // Try to catch the SQL Exception.
        try {

            // Get the result set using the provided SQL Query.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT Appointment_ID " +
                    "FROM appointments " +
                    "WHERE Appointment_ID = ?", strings);

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
     * This method is used to delete an appointment, using the provided SQL Query.
     * Returns a boolean representation of whether the deletion was successful.
     * @param appointment_id The appointment ID of the appointment that will be deleted.
     * @return The boolean representation of whether the appointment was deleted.
     */
    public static boolean deleteAppointment(String appointment_id) {

        // Sets up result for later use.
        int result;

        // Packages the strings for use in the database static class.
        String[] strings = {appointment_id};

        // Get the numeric representation of whether the appointment was deleted.
        result = Database.getInstance().UpdateUsingStrings("DELETE FROM appointments " +
                "WHERE Appointment_ID = ?", strings);

        // return true is result is equal to one.
        return result == 1;
    }

    /**
     * This method is used to check if the selected customer has any appointments.
     * Sets the error text to an empty string.
     * Returns true if no appointments are found.
     * @param customer_id The customer ID of the customer whose appointments must be checked.
     * @param errorText The text for the error to be placed if the customer has appointments.
     * @return The boolean representation of whether the customer has appointments.
     */
    public static boolean hasNoAppointments(String customer_id, Text errorText) {

        // Sets the error text to an empty string.
        errorText.setText("");

        // Packages the strings for use in the database static class.
        String[] strings = {customer_id};

        // Try to catch the SQL Exception.
        try {

            // Get the result set using the provided SQL Query.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT count(*) AS count " +
                    "FROM appointments " +
                    "WHERE Customer_ID = ?", strings);

            // Checks if the result set has a result and that the count is 0.
            if(result.next() && result.getInt("count") == 0){

                // Return true if no appointment is found.
                return true;
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // Set the error message if an appointment is found.
        errorText.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.appointments.hasAppointments"));

        // returns false if appointment is found.
        return false;
    }

    /**
     * This method is used to check if the provided time is within business hours.
     * Returns true if the time is not during business hours.
     * @param time The time combo box to be checked whether it is during business hours.
     * @param errorText The error text to be used to display any business hour related messages.
     * @param type Type of time to be checked.
     * @return The boolean representation of whether the time is within business hours.
     */
    public static boolean isNotDuringBusinessHours(ComboBox<String> time, Text errorText, String type) {

        // Gets the start of business hours in local time.
        String start = LocalDateTime.parse("2020-01-01T08:00:00").atZone(ZoneId.of("America/New_York")).withZoneSameInstant(ZonedDateTime.now().getZone()).toLocalTime().toString().split(":")[0];

        // Get the end if business hours in local time.
        String end = LocalDateTime.parse("2020-01-01T22:00:00").atZone(ZoneId.of("America/New_York")).withZoneSameInstant(ZonedDateTime.now().getZone()).toLocalTime().toString().split(":")[0];

        // Checks if the time combo box is empty, if not checks is the chosen time is within business hours.
        if(!time.getSelectionModel().isEmpty() && Integer.parseInt(LocalDateTime.parse("2020-01-01T" + time.getSelectionModel().getSelectedItem()).atZone(ZonedDateTime.now().getZone()).withZoneSameInstant(ZoneId.of("America/New_York")).toLocalTime().toString().split(":")[0]) >= 8 &&
                Integer.parseInt(LocalDateTime.parse("2020-01-01T" + time.getSelectionModel().getSelectedItem()).atZone(ZonedDateTime.now().getZone()).withZoneSameInstant(ZoneId.of("America/New_York")).toLocalTime().toString().split(":")[0]) <= 21){

            // If not during business hours, return false.
            return false;
        }

        // If during business hours, set error message.
        errorText.setText(type + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.notDuringBusinessHours") + start + ":00 - " + end + ":00");

        // Return true if during business hours.
        return true;
    }

    /**
     * This method is used to check if the provided start or nd time falls within another scheduled appointment.
     * Sets error messaged informing user whether a time is during a scheduled appointment.
     * Returns true if the either time is during a scheduled appointment other than itself.
     * @param appointmentId The appointment ID for the selected appointment.
     * @param customerID The customer of the selected appointment.
     * @param startDate The start date for the appointment.
     * @param endDate The end date for the appointment.
     * @param startTime The start time for the appointment.
     * @param endTime The end time for the appointment.
     * @param startError The start date time error box for start related errors.
     * @param endError The end date time error box for end related errors.
     * @param typeA The String representation of the type/column, Start.
     * @param typeB The String representation of the type/column, End.
     * @return The boolean representation of whether or no the time is during a scheduled appointment.
     */
    public static boolean isTimeDuringScheduledAppointment(String appointmentId, String customerID, LocalDate startDate, LocalDate endDate,
                                                           ComboBox<String> startTime, ComboBox<String> endTime,
                                                           Text startError, Text endError, String typeA, String typeB){

        // Set the appointment scheduled boolean to false for later use.
        boolean appointmentScheduled = false;

        // Checks if the selected customer has any appointments.
        if(getAppointmentCount(customerID) > 0){

            // Set up result for later use.
            ResultSet result;

            // Packages the strings for use in the database static class.
            String[] strings = {customerID};

            // Try to catch the SQL Exception.
            try{

                // Get the result set using the provided SQL Query.
                result = Database.getInstance().getResultsUsingStrings("SELECT appointments.Appointment_ID, appointments.Start, appointments.End " +
                        "FROM appointments " +
                        "WHERE appointments.Customer_ID = ?", strings);

                // While the result has results get the next result.
                while (result.next()){

                    // Set the next appointment ID.
                    String nextAppointment = result.getString("Appointment_ID");

                    // Set the nextAppointments start time.
                    LocalDateTime appointmentStart = LocalDateTime.parse(result.getObject(typeA).toString().replace(" ", "T"));

                    // Set the nextAppointments end time.
                    LocalDateTime appointmentEnd = LocalDateTime.parse(result.getObject(typeB).toString().replace(" ", "T"));

                    // Compare the Selected appointments start date to the next appointments start date and get the numeric result.
                    int startToStart = appointmentStart.toLocalDate().compareTo(startDate);

                    // Compare the Selected appointments start date to the next appointments end date and get the numeric result.
                    int startToEnd = appointmentEnd.toLocalDate().compareTo(startDate);

                    // Compare the Selected appointments end date to the next appointments start date and get the numeric result.
                    int endToStart = appointmentStart.toLocalDate().compareTo(endDate);

                    // Compare the Selected appointments end date to the next appointments end date and get the numeric result.
                    int endToEnd = appointmentEnd.toLocalDate().compareTo(endDate);

                    // Check to make sure the next appointment isn't the selected appointment, the check the date comparison results.
                    if(!appointmentId.equals(nextAppointment) &&
                            (startToStart >= 0 && startToEnd <= 0 || endToStart >= 0 && endToEnd <= 0)) {

                        // Set the appointment start time to the local time.
                        LocalTime appointmentStartTime = appointmentStart.toLocalTime();

                        // Set the appointment end time to the local time.
                        LocalTime appointmentEndTime = appointmentEnd.toLocalTime();

                        // Checks if the start times of the appointments match.
                        if(appointmentStartTime.compareTo(LocalTime.parse(startTime.getSelectionModel().getSelectedItem())) == 0){

                            // Set the error for having same start time.
                            startError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.start") + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.at"));

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }

                        // Checks if the appointment start time is within the appointment window.
                        if (LocalTime.parse(startTime.getSelectionModel().getSelectedItem()).isAfter(appointmentStartTime) &&
                                LocalTime.parse(startTime.getSelectionModel().getSelectedItem()).isBefore(appointmentEndTime)){

                            // Set the error for start time being within appointment window.
                            startError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.start") + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.during"));

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }

                        // Checks if the appointment window is after the start time but before the end time
                        if (appointmentStartTime.isAfter(LocalTime.parse(startTime.getSelectionModel().getSelectedItem())) &&
                                appointmentStartTime.isBefore(LocalTime.parse(endTime.getSelectionModel().getSelectedItem()))){

                            // Set the error for start time being within appointment window.
                            startError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.contains") + " " + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.start") + ".");

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }

                        // Checks if the start times matches the end time of the appointment.
                        if(appointmentStartTime.compareTo(LocalTime.parse(endTime.getSelectionModel().getSelectedItem())) == 0){

                            // Set the error for the start time having the same time as the appointments end time.
                            startError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.end") + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.at"));

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }

                        // Checks if the end times of the appointments match.
                        if(appointmentEndTime.compareTo(LocalTime.parse(endTime.getSelectionModel().getSelectedItem())) == 0){

                            // Set the error for having the same end time.
                            endError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.end") + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.at"));

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }

                        // Checks if the appointment end time is within the appointment window.
                        if(LocalTime.parse(endTime.getSelectionModel().getSelectedItem()).isBefore(appointmentEndTime) &&
                               LocalTime.parse(endTime.getSelectionModel().getSelectedItem()).isAfter(appointmentStartTime)){

                            // Set the error for end time being within appointment window
                            endError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.end") + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.during"));

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }

                        // Checks if the appointment window is before the end an after the start.
                        if(appointmentEndTime.isBefore(LocalTime.parse(endTime.getSelectionModel().getSelectedItem())) &&
                                appointmentEndTime.isAfter(LocalTime.parse(startTime.getSelectionModel().getSelectedItem()))){

                            // Set the error for end time being within appointment window
                            endError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.contains") + " " + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.end") + ".");

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }

                        // Checks if the end times matches the start time of the appointment.
                        if(appointmentEndTime.compareTo(LocalTime.parse(startTime.getSelectionModel().getSelectedItem())) == 0){

                            // Set the error for the end time having the same time as the appointments start time.
                            startError.setText(LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.start") + LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.at"));

                            // Set appointment scheduled to true.
                            appointmentScheduled = true;
                        }
                    }
                }
            }
            catch (SQLException e){

                // Prints the stack trace if query fails.
                e.printStackTrace();
            }
        }

        // Return the appointment scheduled boolean.
        return appointmentScheduled;
    }

    /**
     * This method is used to get the count of how many appointments a customer has.
     * @param customerID The customer ID used to check for an appointment count.
     * @return The numeric representation of how many appointments the customer has.
     */
    private static int getAppointmentCount(String customerID){

        // Set up the result for later.
        ResultSet result;

        // Packages the strings for use in the database static class.
        String[] strings = {customerID};

        // Get the result set using the provided SQL Query.
        result = Database.getInstance().getResultsUsingStrings("SELECT COUNT(*) AS Count FROM appointments WHERE appointments.Customer_ID = ?", strings);

        // Try to catch the SQL Exception.
        try {

            // Checks if the result set has a result.
            if (result.next()){

                // If it has a next result, return the count.
                return result.getInt("Count");
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If fails, return 0.
        return 0;
    }

    /**
     * This method is used to get the appointment count by month.
     * Returns the count for how many appointments are within a given month.
     * @param month The month to be checked.
     * @return The numeric representation of how many appointment a given month has.
     */
    public static int getAppointmentCountByMonth(int month) {

        // Packages the strings for use in the database static class.
        String[] strings = {String.valueOf(month)};

        // Try to catch the SQL Exception.
        try{

            // Get the result set using the provided SQL Query.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT COUNT(*) AS Count " +
                    "FROM appointments " +
                    "WHERE MONTH(Start) = ? AND YEAR(Start) = YEAR(now())", strings);

            // Checks if the result set has a result.
            if (result.next()){

                // If there is a result, return the count.
                return result.getInt("Count");
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If fails, return 0.
        return 0;
    }

    /**
     * This method is used to get the appointment count by type.
     * @param type The type of appointment to be counted.
     * @return The numeric representation of how many appointment a given type has.
     */
    public static int getAppointmentCountByType(String type) {

        // Packages the strings for use in the database static class.
        String[] strings = {type};

        // Try to catch the SQL Exception.
        try{

            // Get the result set using the provided SQL Query.
            ResultSet result = Database.getInstance().getResultsUsingStrings("SELECT COUNT(*) AS Count " +
                    "FROM appointments " +
                    "WHERE Type = ?", strings);

            // Checks if the result set has a result.
            if (result.next()){

                // If there is a result, return the count.
                return result.getInt("Count");
            }
        }
        catch (SQLException e){

            // Prints the stack trace if query fails.
            e.printStackTrace();
        }

        // If fails, return 0.
        return 0;
    }

    /**
     * This method is used to get all the appointments for a contact.
     * Returns the result set of all the appointments for the given contact.
     * @param contactName The name of the contact whose appointments are needed.
     * @return The result set of all the appointments for the given contact.
     */
    public static ResultSet getAllAppointmentsForContact(String contactName) {

        // Packages the strings for use in the database static class.
        String[] strings = {contactName};

        // Return the result set of all the appointments in the database a contact has.
        return Database.getInstance().getResultsUsingStrings("SELECT appointments.Appointment_ID, appointments.Title, " +
                "appointments.Description, appointments.Type, appointments.Start, appointments.End, appointments.Customer_ID " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID " +
                "WHERE contacts.Contact_Name = ?", strings);
    }

    /**
     * This method is used to get all the appointments for a customer.
     * Returns the result set of all the appointments for the given customer.
     * @param customer The name of the customer whose appointments are needed.
     * @return The result set of all the appointments for the given customer.
     */
    public static ResultSet getAllAppointmentsForCustomer(String customer) {

        // Packages the strings for use in the database static class.
        String[] strings = {customer};

        // Return the result set of all the appointments in the database a customer has.
        return Database.getInstance().getResultsUsingStrings("SELECT appointments.Appointment_ID, appointments.Title, " +
                "appointments.Description, appointments.Type, appointments.Location, contacts.Contact_Name, " +
                "appointments.Start, appointments.End, appointments.Customer_ID " +
                "FROM appointments " +
                "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID " +
                "INNER JOIN customers ON appointments.Customer_ID = customers.Customer_ID " +
                "WHERE customers.Customer_Name = ?", strings);
    }
}
