package Classes;

// Javafx import statements needed to run.
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.text.Text;

// Java import statements needed to run.
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Java/MySQL import statements needed to run.
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is for handling the Input and output functionality.
 * @author Dakota Ray Tominus
 */
public class IOHelper extends Helper {

    /**
     * String representation of the class type
     */
    String string = "IO Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public IOHelper(){

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
     * This method is used to Open the generated JavaDoc.
     */
    public static void openJavaDoc() {

        // Try to catch the IOException.
        try{

            // Set new file to be opened.
            File file = new File("C:/Users/Koda/IdeaProjects/SchedulingDesktopApplication/JavaDoc/index.html");

            // Open file in default browser.
            Desktop.getDesktop().browse(file.toURI());
        }
        catch (IOException e){

            // Prints exception stack trace if exception thrown.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to Log a successful login attempt.
     * @param userName The Username of the person logging in.
     */
    public static void logSuccessfulLogin(String userName) {

        // Log the log in attempt.
        logLogin(userName + " " + LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.print.login.success"));
    }

    /**
     * This method is used to Log a failed login attempt.
     * @param userName The Username of the person logging in.
     */
    public static void logFailedLogin(String userName) {

        // Log the log in attempt.
        logLogin(userName + " " + LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.print.login.failed"));
    }

    /**
     * This method is used to log the login attempt to a file.
     * @param message The message to be logged in the file.
     */
    public static void logLogin(String message) {

        // Sets up the date time formatter.
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss MM/dd/yyyy");

        // Adds the formatted time to the message as well as a new line.
        message = message + format.format(LocalDateTime.now()) + System.getProperty("line.separator");

        // Try to catch the IOException.
        try{

            // Create new file object for use.
            File file = new File("login_activity.txt");

            // Checks if the file needs to be created and creates it if needed.
            if(file.createNewFile()){

                // If file was created, log the login attempt.
                Files.write(file.toPath(), ("Login Attempt History" + System.getProperty("line.separator")).getBytes(), StandardOpenOption.WRITE);
            }
            else{

                // If not created, append the file with the login attempt.
                Files.write(file.toPath(), message.getBytes(), StandardOpenOption.APPEND);
            }

        }
        catch (IOException e){

            // Prints exception stack trace if exception thrown.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to populate a table with the given result set.
     * Columns in table must have ID's that match result columns.
     * @param table The table the results are to be added too.
     * @param results The result to be added to the tables.
     */
    public static void populateTableWithResults(TableView<Map<String, String>> table, ResultSet results) {

        // Clears the table to be used.
        table.getItems().clear();

        // Try to catch the SQLException.
        try{

            // Sets up the table foe use.
            setUpTable(table);

            // Creates a new data list to be appended.
            ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();

            // While the results still have a next row.
            while (results.next()){

                // Create a new row to be added to the table data set.
                tableData.add(createNewDataRow(table, results));
            }

            // Set the table data set.
            table.setItems(tableData);
        }
        catch (SQLException e){

            // Prints exception stack trace if exception thrown.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to create a new data row from a result set.
     * @param table The table the row is to be added to.
     * @param results The results to be added to the table.
     * @return The row of data from the results for the table.
     */
    private static Map<String, String> createNewDataRow(TableView<Map<String, String>> table, ResultSet results) {

        // Tru to catch the SQLException.
        try{

            // Create a new hash map for the row.
            Map<String, String> row = new HashMap<>();

            // For each column in the table.
            for (TableColumn<Map<String, String>, ?> column: table.getColumns()) {

                // Get the data for that column, and add it to the row using the column ID as the key.
                row.put(column.getId(), results.getObject(column.getId()).toString());
            }

            // Return the row once created.
            return row;
        }
        catch (SQLException e){

            // Prints exception stack trace if exception thrown.
            e.printStackTrace();
        }

        // Return null if all fails.
        return null;
    }

    /**
     * This method is used to set up the table to have data added programmatically.
     * @param table The table to be set up.
     */
    private static void setUpTable(TableView<Map<String, String>> table) {

        // For each column in the table.
        for (TableColumn<Map<String, String>, ?> column: table.getColumns()) {

            // Set the type of data using the columns given ID.
            column.setCellValueFactory(new MapValueFactory(column.getId()));
        }
    }

    /**
     * This method is used to Populate the given combo box with results.
     * @param comboBox The combo box to be populated.
     * @param results The results to be added to the combo box.
     */
    public static void populateComboBoxWithResults(ComboBox<String> comboBox, ResultSet results) {

        // Try to catch the SQLException.
        try{

            // While the result set has a next.
            while (results.next()){

                // Add the result to the combo box.
                comboBox.getItems().add(results.getString(comboBox.getId()));
            }
        }
        catch (SQLException e){

            // Prints exception stack trace if exception thrown.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to clear a given combo box of it's selected items.
     * @param box The combo box to be cleared.
     */
    public static void clearComboBox(ComboBox<String> box){

        // Set's the combo box to be uneditable.
        box.setEditable(true);

        // Clears the combo box of it's selected items.
        box.getSelectionModel().clearSelection();

        // Set's the combo box to be editable.
        box.setEditable(false);
    }

    /**
     * This method is used to empty a given combo box of its items.
     * @param box The combo box to be emptied.
     */
    public static void emptyComboBox(ComboBox<String> box){

        // Set's the combo box to be uneditable.
        box.setEditable(true);

        // Empties the combo box of its  items.
        box.getItems().clear();

        // Set's the combo box to be editable.
        box.setEditable(false);
    }

    /**
     * This method is used to focus on an element.
     * @param node The element to be focused on.
     */
    public static void focusOnElement(Node node) {

        // Focus on the node.
        node.requestFocus();
    }

    /**
     * This method is used to check if the given string is a valid string.
     * Returns true if the string is a valid string.
     * @param string The string to be checked.
     * @param stringError The error text used to display strings related error.
     * @param field The string representation of the text field being checked.
     * @return The boolean representation of whether the string is a valid string.
     */
    public static boolean isStringValid(TextField string, Text stringError, String field) {

        // Sets the error text to an empty string.
        stringError.setText("");

        // If the name fields text is null or a length of 0, indicating nothing is input.
        if(string.getText() == null || string.getText().trim().length() == 0){

            // Displays errors message if nothing is found.
            stringError.setText(LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.string.empty") + field + ".");

            // Return false if empty or null.
            return false;
        }

        // Checks if the name fields text is an integer.
        if(isInteger(string.getText())){

            // Displays errors message if integer is found.
            stringError.setText(field + LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.string.integer"));

            // Return false if number was provided.
            return false;
        }

        // Returns true if string is valid.
        return true;
    }

    /**
     * This method is used to check if an address is valid or not.
     * @param address The address to be checked.
     * @param addressError The error text used to display address related errors.
     * @return The boolean representation of whether the address is valid.
     */
    public static boolean isAddressValid(TextField address, Text addressError) {

        // Sets the error text to a blank string.
        addressError.setText("");

        // Sets up the regex pattern to be used.
        Pattern patter = Pattern.compile("\\d{1,9}\\s(\\b\\w*\\b\\s){1,2}\\w*\\.?,\\s(\\b\\w*\\b\\s){1,2}", Pattern.CASE_INSENSITIVE);

        // Sets up the matcher.
        Matcher match = patter.matcher(address.getText());

        // If no match found.
        if(!match.find()){

            // Set error text.
            addressError.setText(LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.address.valid"));

            // Return false.
            return false;
        }

        // If matches, return true.
        return true;
    }

    /**
     * This method is used to check if a combo box is unselected.
     * @param comboBox The combo box to be checked.
     * @param comboBoxError The error text to display combo box related error.
     * @param field The string representation of what type of combo box is provided.
     * @return The boolean representation of whether the combo box has no selection.
     */
    public static boolean isComboBoxUnselected(ComboBox<String> comboBox, Text comboBoxError, String field) {

        // Sets the error text to an empty string.
        comboBoxError.setText("");

        // Checks if the combo box has a selection.
        if(comboBox.getSelectionModel().isEmpty()){

            // If nothing is selected set error.
            comboBoxError.setText(LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.comboBox.noSelection") + field + ".");

            // Return true if it has no selection.
            return true;
        }

        // Return false if it has a selection.
        return false;
    }

    /**
     * This method is used to check if the provided postal code is a valid postal code.
     * @param postalCode The postal code to be checked.
     * @param postalCodeError The error text to be display postal code related errors.
     * @return The boolean representation of whether the postal code is valid.
     */
    public static boolean isPostalCodeValid(TextField postalCode, Text postalCodeError) {

        // Sets the error text to an empty string.
        postalCodeError.setText("");

        // Sets up the regex pattern to be used.
        Pattern patter = Pattern.compile("\\d{5}|\\d{5}-\\d{4}", Pattern.CASE_INSENSITIVE);

        // Sets up the matcher.
        Matcher match = patter.matcher(postalCode.getText());

        // If no match found.
        if(!match.find()){

            // Set error text.
            postalCodeError.setText(LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.postalCode.invalid"));

            // Return false.
            return false;
        }

        // If matches, return true.
        return true;
    }

    /**
     * This method is used to check if the provided phone number is valid.
     * @param phone The phone number to be checked.
     * @param phoneError The error text to display phone number related errors.
     * @return The boolean representation of whether the phone number is valid.
     */
    public static boolean isPhoneValid(TextField phone, Text phoneError) {

        // Sets the error text to an empty string.
        phoneError.setText("");

        // Sets up the regex pattern to be used.
        Pattern patter = Pattern.compile("\\d{3}-\\d{3}-\\d{4}", Pattern.CASE_INSENSITIVE);

        // Sets up the matcher.
        Matcher match = patter.matcher(phone.getText());

        // If no match found.
        if(!match.find()){

            // Set error text.
            phoneError.setText(LanguageHelper.getResourceBundle().getString("ioHelper.errorMessage.phoneNumber.invalid"));

            // Return false.
            return false;
        }

        // If matches, return true.
        return true;
    }

    /**
     * This method checks if a String is an Integer or not.
     * Returns true if an integer is found.
     * @param string The string to be checked for an integer.
     * @return A boolean representation of if the String is an Integer.
     */
    public static boolean isInteger(String string){

        // Try to catch the NumberFormatException.
        try{

            // Casts the String to an Integer.
            Integer.parseInt(string);
        }
        catch(NumberFormatException e){

            // If the casting fails because of a number format issue, return false.
            return false;
        }

        // If everything works, return true.
        return true;
    }

    /**
     * This method is used to set the given combo box to the given index.
     * @param comboBox combo box to be set.
     * @param index Index to set the combo box to.
     */
    public static void setComboBox(ComboBox<String> comboBox, int index) {

        // Set the combo box to the given index.
        comboBox.getSelectionModel().select(index);
    }

    /**
     * This method is used to toggle the radio buttons.
     * @param radio Radio button to be set.
     * @param Radios The list of radio buttons to be checked to unselect.
     */
    public static void toggleRadioButtons(RadioButton radio, ObservableList<RadioButton> Radios){

        // For each radio button in the list of radio buttons.
        for (RadioButton radioButton: Radios) {

            // Check if the radio button is the given radio button.
            if(radio != radioButton){

                // If not the given button, set to unselected.
                radioButton.setSelected(false);
            }
        }
    }

    /**
     * This method is used to get a value from the provided column, from the selected row.
     * @param table The tables whose row has been selected.
     * @param column The column from which data is needed.
     * @return The string representation of the selected rows given column data.
     */
    public static String getValueFromRowColumn(TableView<Map<String, String>> table, String column){

        return table.getSelectionModel().getSelectedItem().get(column);
    }
}
