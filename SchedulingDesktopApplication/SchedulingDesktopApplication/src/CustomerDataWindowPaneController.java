import Classes.*;

// Javafx import statements needed to run.
import com.google.protobuf.NullValue;
import com.mysql.cj.protocol.Resultset;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

// Java import statements needed to run.
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;

/**
 * This class is for handling the event and functionality for the Customer Data Window pane.
 * This class links to CustomerDataWindowPane.FXML using @FXML,OnAction, fx:id and fx:controller.
 * @author Dakota Ray Tominus
 */
public class CustomerDataWindowPaneController {

    // XLM location of the Appointment Report window pane
    private final String appointmentReportXMLLocation = "/Scenes/AppointmentReportWindowPane.fxml";

    // XLM location of the Contact report window pane
    private final String contactReportXMLLocation = "/Scenes/ContactScheduleReportWindowPane.fxml";

    // XLM location of the contact history report window pane
    private final String contactHistoryReportXMLLocation = "/Scenes/CustomerSchedulingHistoryReportWindowPane.fxml";

    // String representation of an empty string
    private final String emptyString = "";

    // String representation of the customer ID column name
    private final String customerIDColumn = "Customer_ID";

    // String representation of the Customer name columns name
    private final String customerNameColumn = "Customer_Name";

    // String representation of the customer address column name
    private final String customerAddressColumn = "Address";

    // String representation of the customer postal code column name
    private final String customerPostalCodeColumn = "Postal_Code";

    // String representation of the customer phone column name
    private final String customerPhoneColumn = "Phone";

    // String representation of the customer country column name
    private final String countryColumn = "Country";

    // String representation of the customer division column name
    private final String divisionColumn = "Division";

    // String representation of the Key to the addition string
    private final String additionKey = "customers.controller.string.addition";

    // String representation of the Key to the deletion string
    private final String deletionKey = "customers.controller.string.deletion";

    // String representation of the Key to the no selection string
    private final String noSelectionKey = "customers.controller.string.noSelection";

    // String representation of the starting bracket
    private final String startingBracketString = " (";

    // String representation of the Key to the appointment prompt string
    private final String appointmentPromptKey = "textField.appointments.prompt.id";

    // String representation of the closing bracket
    private final String closingBracketString = ")";

    // String representation of the Key to the customer name string
    private final String customerNameKey = "customers.controller.string.customerName";

    // String representation of the Key to the customer country string
    private final String customerCountryKey = "customers.controller.string.country";

    // String representation of the Key to the customer division string
    private final String customerDivisionKey = "customers.controller.string.division";

    // The root object of the scene.
    @FXML
    Parent root;

    // The customer search field
    @FXML
    TextField customerSearchField;

    // The customer data table.
    @FXML
    TableView<Map<String, String>> customerDataTable;

    // The text used for table related error messages.
    @FXML
    Text tableError;

    // The text field used for the customer ID, auto generated.
    @FXML
    TextField customerID;

    // The text used for customer ID related error messages.
    @FXML
    Text customerIDError;

    // The text field used for the customer name.
    @FXML
    TextField customerName;

    // The text used for Customer name related error messages.
    @FXML
    Text customerNameError;

    // The text field used for the address.
    @FXML
    TextField address;

    // The text used for address related error messages.
    @FXML
    Text addressError;

    // The text field used for the postal code.
    @FXML
    TextField postalCode;

    // The text used for postal code related error messages.
    @FXML
    Text postalCodeError;

    // The text field used for the phone number.
    @FXML
    TextField phone;

    // The text used for phone number related error messages.
    @FXML
    Text phoneError;

    // The combo box used for the country.
    @FXML
    ComboBox<String> country;

    // The text used for country related error messages.
    @FXML
    Text countryError;

    // The combo box used for the first level division.
    @FXML
    ComboBox<String> division;

    // The text used for first level division related error messages.
    @FXML
    Text divisionError;

    /**
     * This method runs everytime the pane is loaded.
     * Populates customer data.
     * Sets up a listener.
     * Populates the combo boxes.
     * Sets up the page for a new customer.
     */
    @FXML
    private void initialize(){

        // Populates the customer data table.
        populateCustomerData();

        // Sets up the listener for the table.
        setTableListener();

        // Populates the combo boxes with information.
        populateComboBoxes();

        // Sets up the page for a new customer to be added.
        addCustomer();

        setUpSearch();
    }

    /**
     * This method is used to set the page up for the next customer to be added.
     * Clears all fields.
     * Sets the next customer ID to the customer id field.
     * Focuses on the customer name field.
     */
    @FXML
    private void addCustomer(){

        // Clears all the fields.
        clearCustomerDataFields();

        // Sets up the next customer ID(may change at injection)
        setNextCustomerId(nextId());

        // Focuses on the customer name field.
        focusOnCustomerName();
    }

    /**
     * This method is used to save the customer data to the database.
     * Validates the user input.
     * If successful insertion, clears the fields and repopulates the data table.
     * Alerts user to addition.
     */
    @FXML
    private void saveCustomerData(){

        // Validate the input data.
        if(validateCustomerData()){

            // Add data using Customer Helper class to access database, success with be evaluated into a boolean representation.
            if(CustomerHelper.saveCustomerData(customerID.getText(), customerName.getText(), address.getText(),
                    postalCode.getText(), phone.getText(), FirstLevelDivisionHelper.getDivisionID(division.getSelectionModel().getSelectedItem()))){

                // If successful clear all the data fields.
                clearCustomerDataFields();

                // Alert to successful addition.
                tableError.setText(LanguageHelper.getResourceBundle().getString(additionKey));

                // Populates the customer data table.
                populateCustomerData();
            }
        }
    }

    /**
     * This method is used to delete a customer from the database.
     * Checks if the selection is empty.
     * Checks if the customer has appointments and evaluates the success of the deletion.
     * If successful alerts the user, clears the fields and repopulates the Customer data table.
     * Alerts user if no selection has been made.
     */
    @FXML
    private void deleteCustomer(){

        // Check to see if appointment is selected.
        if(customerDataTable.getSelectionModel().isEmpty()){

            // Alert to the failed deletion.
            tableError.setText(LanguageHelper.getResourceBundle().getString(noSelectionKey));

            // Return to cancel the operation.
            return;
        }

        // Checks if the customer has any appointments, if not, evaluates to true if it was deleted.
        if(AppointmentHelper.hasNoAppointments(IOHelper.getValueFromRowColumn(customerDataTable, customerIDColumn), tableError) &&
                CustomerHelper.deleteCustomer(IOHelper.getValueFromRowColumn(customerDataTable, customerIDColumn))){

            // If successful clear all the data fields.
            clearCustomerDataFields();

            // Alert to successful deletion.
            tableError.setText(LanguageHelper.getResourceBundle().getString(deletionKey));

            // Populates the customer data table.
            populateCustomerData();
        }
    }

    /**
     * This method is used to set up the Scheduling data scene.
     * Opens a new Scheduling data window pane using the scene manager.
     * catches IOException e and prints the stack trace.
     */
    @FXML
    private void viewAppointments(){

        // Try to catch IOException.
        try{

            // Open new scene using the scene manager class.
            // XLM location of the Scheduling Data window pane
            String schedulingDataXMLLocation = "/Scenes/SchedulingDataWindowPane.fxml";
            SceneManager.openScene(root, new Scene(FXMLLoader.load(getClass().getResource(schedulingDataXMLLocation),  LanguageHelper.getResourceBundle())));
        }
        catch (IOException e){

            // Prints caught exception if Scheduling Data window pane fails to load.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to set up the Appointments report scene.
     * Opens a new Appointment Report Window Pane.
     * catches IOException e and prints the stack trace.
     */
    @FXML
    private void getReportAppointments(){

        // Try to catch IOException.
        try{

            // Open new scene using the scene manager class.
            SceneManager.openReport(new Scene(FXMLLoader.load(getClass().getResource(appointmentReportXMLLocation),  LanguageHelper.getResourceBundle())));
        }
        catch (IOException e){

            // Prints caught exception if Appointment report window pane fails to load.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to set up the Contact Schedule report scene.
     * Opens a new Contact Schedule Report Window Pane.
     * catches IOException e and prints the stack trace.
     */
    @FXML
    private void getReportContactSchedule(){

        // Try to catch IOException.
        try{

            // Open new scene using the scene manager class.
            SceneManager.openReport(new Scene(FXMLLoader.load(getClass().getResource(contactReportXMLLocation),  LanguageHelper.getResourceBundle())));
        }
        catch (IOException e){

            // Prints caught exception if Contact scheduling report window pane fails to load.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to set up the Customer history report scene.
     * Opens a new Customer Scheduling History Report Window Pane.
     * catches IOException e and prints the stack trace.
     */
    @FXML
    private void getReportCustomerHistory(){

        // Try to catch IOException.
        try{

            // Open new scene using the scene manager class.
            SceneManager.openReport(new Scene(FXMLLoader.load(getClass().getResource(contactHistoryReportXMLLocation),  LanguageHelper.getResourceBundle())));
        }
        catch (IOException e){

            // Prints caught exception if Scheduling history report window pane fails to load.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to open the JavaDoc.
     * Uses the IOHelper class to open the JavaDoc in a Browser window.
     */
    @FXML
    private void openJavaDoc() {

        // Opens JavaDoc in the default browser.
        IOHelper.openJavaDoc();
    }

    /**
     * This method is used to populate the customer data table.
     * Gets the customers from the database using the customer helper class.
     * Fills the table with the results, using the IOHelper.
     */
    private void populateCustomerData() {

        // Gets all the customers from the database using the customer helper class.
        ResultSet results = CustomerHelper.getAllCustomers();

        // Populates all the customers into the customer data table, using the IOHelper class.
        IOHelper.populateTableWithResults(customerDataTable, results);
    }

    /**
     * This method is used to set up the search functionality for the customer data window pane
     */
    private void setUpSearch() {

        customerSearchField.textProperty().addListener((obj, oldVal, newVal) -> searchCustomers(newVal));
    }

    /**
     * This method is used to search through customer data for possible matches and initiate the population fo the new dataset
     * @param newVal The new value to be used in the search
     */
    private void searchCustomers(String newVal) {

        ResultSet results = null;

        // Checks if the provided new string is empty after removing any encapsulating white space
        if(!newVal.trim().isEmpty()){

            // SEARCH
            results = CustomerHelper.getSearchResults(newVal);


        }
        else{

            results = CustomerHelper.getAllCustomers();
        }

        if(results != null){

            IOHelper.populateTableWithResults(customerDataTable, results);
        }
    }

    /**
     * This method is used to set up a table listener on the customer data table.
     * Listener will listen for a selection, and fill in the selected customer data to the corresponding text fields.
     * *** LAMBDA EXPRESSION*** This lambda expression simplifies the code while setting up much-needed functionality.
     */
    private void setTableListener() {

        // Sets up listener to populate the selected customer's data into the text fields when a customer is selected.
        customerDataTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> fillInSelectedCustomerData());
    }

    /**
     * This method is used to populate all initially populated combo boxes.
     * Calls methods to populate combo boxes.
     *** ADD NEW COMBO BOX POPULATION METHODS HERE.***
     */
    private void populateComboBoxes() {

        // Call to populate the country combo box.
        populateCountryComboBox();
    }

    /**
     * This method is used to populate the country combo box.
     * Gets all countries from the database using the country helper class.
     * populates country combo box with results.
     */
    private void populateCountryComboBox() {

        // Gets all the available country combo box choices from the database using the country helper class.
        ResultSet result = CountryHelper.getAllCountries();

        // Populates the country combo box with the results.
        IOHelper.populateComboBoxWithResults(country, result);
    }

    /**
     * This method us used to populate the first level division combo box.
     * Checks if the country has been selected.
     * If it has, populate the first level division combo box with first level divisions related to selected country.
     */
    @FXML
    private void populateDivisionComboBox() {

        // Checks if there is a country selection.
        if(!country.getSelectionModel().isEmpty()){

            // Clears the first level division combo box to start with a fresh list.
            division.getItems().clear();

            // Gets all the first level divisions related to the selected country from the database using the First level division helper class.
            ResultSet result = FirstLevelDivisionHelper.getAllFirstLevelDivisions(CountryHelper.getCountryID(country.getSelectionModel().getSelectedItem()));

            // Populates the first level division combo box with results.
            IOHelper.populateComboBoxWithResults(division, result);
        }
    }

    /**
     * This method is used to clear the customer data fields.
     * Calls a method to reset the combo boxes.
     * Sets all the text and text fields to an empty string.
     */
    private void clearCustomerDataFields() {

        // Set customer ID Text field to an empty string.
        customerID.setText(emptyString);

        // Set customer ID error Text to an empty string.
        customerIDError.setText(emptyString);

        // Set customer name Text field to an empty string.
        customerName.setText(emptyString);

        // Set customer name error Text to an empty string.
        customerNameError.setText(emptyString);

        // Set address Text field to an empty string.
        address.setText(emptyString);

        // Set address error Text to an empty string.
        addressError.setText(emptyString);

        // Resets the combo boxes.
        resetComboBoxes();

        // Set postal code Text field to an empty string.
        postalCode.setText(emptyString);

        // Set postal code error Text to an empty string.
        postalCodeError.setText(emptyString);

        // Set phone Text field to an empty string.
        phone.setText(emptyString);

        // Set phone error Text to an empty string.
        phoneError.setText(emptyString);
    }

    /**
     * This method resets the combo boxes.
     * Calls IOHelper class to clear and empty combo boxes.
     * Resets the corresponding error text.
     */
    private void resetComboBoxes() {

        // Uses IOHelper class to clear the country combo box.
        IOHelper.clearComboBox(country);

        // Sets the country error Text to an empty string.
        countryError.setText(emptyString);

        // Uses IOHelper class to empty the division combo box.
        IOHelper.emptyComboBox(division);

        // Sets the division error Text to an empty string.
        divisionError.setText(emptyString);
    }

    /**
     * This method is used to fill in the selected customer's data to the customer data fields using the IO Helper class.
     * Checks if the customer table selection is empty and cancels the operation if true.
     * Clears all customer data fields.
     */
    private void fillInSelectedCustomerData() {

        // Checks if the customer data table has a selection.
        if(customerDataTable.getSelectionModel().isEmpty()){

            // Cancels the operation if true.
            return;
        }

        // Clears all the customer data fields to be populated with selected customer data.
        clearCustomerDataFields();

        // Sets the customer ID to the selected customers.
        customerID.setText(IOHelper.getValueFromRowColumn(customerDataTable, customerIDColumn));

        // Sets the customer name to the selected customers.
        customerName.setText(IOHelper.getValueFromRowColumn(customerDataTable, customerNameColumn));

        // Sets the address to the selected customers.
        address.setText(IOHelper.getValueFromRowColumn(customerDataTable, customerAddressColumn));

        // Fills in the combo boxes with selected customer data.
        fillInComboBoxes();

        // Sets the postal code to the selected customers.
        postalCode.setText(IOHelper.getValueFromRowColumn(customerDataTable, customerPostalCodeColumn));

        // Sets the phone number to the selected customers.
        phone.setText(IOHelper.getValueFromRowColumn(customerDataTable, customerPhoneColumn));
    }

    /**
     * This method is used to fill in customer data to the combo boxes.
     * Uses the IOHelper class to set the customer's data into the corresponding combo boxes.
     * Populates the division combo box.
     *** ADD NEW COMBO BOX FILL METHODS HERE ***
     */
    private void fillInComboBoxes() {

        // Uses the IO Helper class to set the country combo box with customer data.
        IOHelper.setComboBox(country, country.getItems().indexOf(IOHelper.getValueFromRowColumn(customerDataTable, countryColumn)));

        // Populate the division combo box.
        populateDivisionComboBox();

        // Uses the IO Helper class to set the division combo box with customer data.
        IOHelper.setComboBox(division, division.getItems().indexOf(IOHelper.getValueFromRowColumn(customerDataTable, divisionColumn)));
    }

    /**
     * This method is used to get the hypothetical next ID from the database.
     * Uses the Customer Helper class to get the next ID.
     * @return String representation of the next ID.
     */
    private String nextId() {

        // Returns the hypothetical next ID from the database.
        return CustomerHelper.generateNextCustomerId();
    }

    /**
     * This method is used to set the next customer id.
     * Uses the given string to set the customer ID text field, includes message about it being generated.
     * @param nextId The string representation of the next hypothetical customer ID.
     */
    private void setNextCustomerId(String nextId) {

        // Sets the customer ID text field to the hypothetical next customer ID with a message about it being generated.
        customerID.setText(nextId + startingBracketString + LanguageHelper.getResourceBundle().getString(appointmentPromptKey) + closingBracketString);
    }

    /**
     * This method is used to focus on the customer name field.
     * Uses IOHelper to focus on the customer name field.
     */
    private void focusOnCustomerName() {

        // Focuses on customer name text field.
        IOHelper.focusOnElement(customerName);
    }

    /**
     * This method is used to validate the user input data for the customer, and display error where appropriate.
     * Sets up a boolean isValid to keep track of validation throughout method.
     * Calls individual validation methods from the IOHelper class.
     * sets isValid to false if any validation fails.
     * Returns true if all data is valid.
     * @return The boolean representation of whether or no all data was validated successfully.
     */
    private boolean validateCustomerData() {

        // Sets up the boolean used to keep track of the validation success status.
        boolean isValid = IOHelper.isStringValid(customerName, customerNameError, LanguageHelper.getResourceBundle().getString(customerNameKey));

        // Uses IOHelper class to check if the name is a valid string.
        // Sets the isValid boolean to false if the customer name data is not valid.

        // Uses IOHelper class to check if the address is a valid address.
        if(!IOHelper.isAddressValid(address, addressError)){

            // Sets the isValid boolean to false if the address data is not valid.
            isValid = false;
        }

        // Uses IOHelper class to check if the country is selected.
        if(IOHelper.isComboBoxUnselected(country, countryError, LanguageHelper.getResourceBundle().getString(customerCountryKey))){

            // Sets the isValid boolean to false if the country is not selected.
            isValid = false;
        }

        // Uses IOHelper class to check if the division is selected.
        if(IOHelper.isComboBoxUnselected(division, divisionError, LanguageHelper.getResourceBundle().getString(customerDivisionKey))){

            // Sets the isValid boolean to false if the division is not selected.
            isValid = false;
        }

        // Uses IOHelper class to check if the postal code is a valid postal code.
        if(!IOHelper.isPostalCodeValid(postalCode, postalCodeError)){

            // Sets the isValid boolean to false if the postal code is not valid.
            isValid = false;
        }

        // Uses IOHelper class to check if the phone is a valid phone number.
        if(!IOHelper.isPhoneValid(phone, phoneError)){

            // Sets the isValid boolean to false if the phone number is not valid.
            isValid = false;
        }

        // returns is valid.
        return isValid;
    }
}
