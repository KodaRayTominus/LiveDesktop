import Classes.*;

// Javafx import statements needed to run.
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;

// Java import statements needed to run.
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * This class is for handling the event and functionality for the Scheduling data Window pane.
 * This class links to SchedulingDataWindowPane.FXML using @FXML, OnAction, fx:id and fx:controller.
 * @author Dakota Ray Tominus
 */
public class SchedulingDataWindowPaneController {

    // The root object of the scene.
    @FXML
    Parent root;

    // The scheduling by month toggle
    @FXML
    RadioButton byMonthToggle;

    // The scheduling by week toggle
    @FXML
    RadioButton byWeekToggle;

    // The all scheduling toggle
    @FXML
    RadioButton allToggle;

    // The Schedule data table.
    @FXML
    TableView<Map<String, String>> schedulingDataTable;

    // The text used for table related error messages.
    @FXML
    Text tableError;

    // The text field used for the appointment ID, auto generated.
    @FXML
    TextField appointmentID;

    // The text field used for the appointment title.
    @FXML
    TextField appointmentTitle;

    // The text used for appointment title related error messages.
    @FXML
    Text appointmentTitleError;

    // The text field used for the appointment description.
    @FXML
    TextField appointmentDescription;

    // The text used for appointment description related error messages.
    @FXML
    Text appointmentDescriptionError;

    // The text field used for the appointment location.
    @FXML
    TextField appointmentLocation;

    // The text used for appointment location related error messages.
    @FXML
    Text appointmentLocationError;

    // The combo box used for the appointment contact.
    @FXML
    ComboBox<String> appointmentContact;

    // The text used for appointment contact related error messages.
    @FXML
    Text appointmentContactError;

    // The combo box used for the appointment type.
    @FXML
    ComboBox<String> appointmentType;

    // The text used for appointment type related error messages.
    @FXML
    Text appointmentTypeError;

    // The date picker for the start date.
    @FXML
    DatePicker startDate;

    // The combo box used for the appointments start time.
    @FXML
    ComboBox<String> startTime;

    // The text used for appointments start time related error messages.
    @FXML
    Text startTimeError;

    // The combo box used for the appointments end time.
    @FXML
    ComboBox<String> endTime;

    // The date picker for the start date.
    @FXML
    DatePicker endDate;

    // The text used for appointment end time related error messages.
    @FXML
    Text endTimeError;

    // The combo box used for the customer ID.
    @FXML
    ComboBox<String> customerID;

    // The text used for Customer ID related error messages.
    @FXML
    Text customerIDError;

    // The combo box used for the user ID.
    @FXML
    ComboBox<String> userID;

    // The text used for user ID related error messages.
    @FXML
    Text userIDError;

    /**
     * This method runs everytime the pane is loaded.
     * Toggles the all scheduling toggle.
     * Populates scheduling data.
     * Sets up a listener.
     * Populates the combo boxes.
     * Sets up the page for a new appointment.
     */
    @FXML
    private void initialize(){

        // Toggle the "All" radio button.
        allToggle.setSelected(true);

        // Populates the scheduling data table.
        populateSchedulingData();

        // Sets up the listener for the table.
        setTableListener();

        // Populates the combo boxes with information.
        populateComboBoxes();

        // Sets up the page for a new appointment to be added.
        addAppointment();
    }

    /**
     * This method is used to set up the Customer data scene.
     * Opens a new Scheduling data window pane using the scene manager.
     * catches IOException e and prints the stack trace.
     */
    @FXML
    private void viewCustomers(){

        // Try to catch IOException.
        try{

            // Open new scene using the scene manager class.
            SceneManager.openScene(root, new Scene(FXMLLoader.load(getClass().getResource("/Scenes/CustomerDataWindowPane.fxml"),  LanguageHelper.getResourceBundle())));
        }
        catch (IOException e){

            // Prints caught exception if Customer Data window pane fails to load.
            e.printStackTrace();
        }
    }

    /**
     * This method is used to set the page up for the next schedule to be added.
     * Clears all fields.
     * Sets the next appointment ID to the appointment id field.
     * Focuses on the appointment title field.
     */
    @FXML
    private void addAppointment(){

        // clear all fields
        clearAppointmentDataFields();

        // Sets up the next appointment ID(may change at injection)
        setNextAppointmentId(nextId());

        // Focuses on the appointment title field.
        focusOnAppointmentTitle();
    }

    /**
     * This method is used to save the appointment data to the database.
     * Validates the user input.
     * If successful insertion, clears the fields and repopulates the data table.
     * Alerts user to addition.
     */
    @FXML
    private void saveAppointmentData(){

        // Validate the input data.
        if(validateAppointmentData()){

            // Add data using Appointment Helper class to access database, success with be evaluated into a boolean representation.
            if(AppointmentHelper.saveAppointmentData(appointmentID.getText(), appointmentTitle.getText(), appointmentDescription.getText(),
                    appointmentLocation.getText(), appointmentContact.getSelectionModel().getSelectedItem(), appointmentType.getSelectionModel().getSelectedItem(),
                    (startDate.getValue() + "T" + startTime.getSelectionModel().getSelectedItem()),(endDate.getValue() + "T" +
                    endTime.getSelectionModel().getSelectedItem()), ZonedDateTime.now().getOffset().toString(), customerID.getSelectionModel().getSelectedItem(),
                    userID.getSelectionModel().getSelectedItem())){

                // If successful clear all the data fields.
                clearAppointmentDataFields();

                // Alert to successful addition.
                tableError.setText(LanguageHelper.getResourceBundle().getString("scheduling.controller.string.addition"));

                // Populates the scheduling data table.
                populateSchedulingData();
            }
        }
    }

    /**
     * This method is used to delete an appointment from the database.
     * Checks if the selection is empty.
     * Evaluates the success of the deletion.
     * If successful alerts the user, clears the fields and repopulates the Schedule data table.
     * Alerts user if no selection has been made.
     */
    @FXML
    private void deleteAppointment(){

        // Check to see if appointment is selected.
        if(schedulingDataTable.getSelectionModel().isEmpty()){

            // Alert to the failed deletion.
            tableError.setText(LanguageHelper.getResourceBundle().getString("scheduling.controller.string.noSelection"));

            // Return to cancel the operation.
            return;
        }

        // Evaluates to true if it was deleted.
        if(AppointmentHelper.deleteAppointment(IOHelper.getValueFromRowColumn(schedulingDataTable, "Appointment_ID"))){

            // Alert to successful deletion.
            tableError.setText(appointmentType.getSelectionModel().getSelectedItem() + LanguageHelper.getResourceBundle().getString("scheduling.controller.string.deletionAppointment") + " " + appointmentID.getText() + LanguageHelper.getResourceBundle().getString("scheduling.controller.string.deletion"));

            // If successful clear all the data fields.
            clearAppointmentDataFields();

            // Populates the customer data table.
            populateSchedulingData();
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
            SceneManager.openReport(new Scene(FXMLLoader.load(getClass().getResource("/Scenes/AppointmentReportWindowPane.fxml"),  LanguageHelper.getResourceBundle())));
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
            SceneManager.openReport(new Scene(FXMLLoader.load(getClass().getResource("/Scenes/ContactScheduleReportWindowPane.fxml"),  LanguageHelper.getResourceBundle())));
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
            SceneManager.openReport(new Scene(FXMLLoader.load(getClass().getResource("/Scenes/CustomerSchedulingHistoryReportWindowPane.fxml"),  LanguageHelper.getResourceBundle())));
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
     * This method is used to populate the scheduling data table.
     * Gets the appointments from the database using the appointment helper class.
     * Uses the toggle to discern how much of the data to pull.
     * Fills the table with the results, using the IOHelper.
     */
    private void populateSchedulingData() {

        // Sets up results as null for a later decision.
        ResultSet results = null;

        // Checks if the all radio button is toggled.
        if(allToggle.isSelected()){

            // Get all the appointments using customer helper class.
            results = AppointmentHelper.getAllAppointments();
        }

        // Checks if the month radio button is toggled.
        if(byMonthToggle.isSelected()){

            // Get the months appointments using customer helper class.
            results = AppointmentHelper.getMonthsAppointments();
        }

        // Checks if the week radio button is toggled.
        if(byWeekToggle.isSelected()){

            // Get the weeks appointments using customer helper class.
            results = AppointmentHelper.getWeeksAppointments();
        }

        // Checks if result is still null.
        if(results != null){

            // If not null populate the table with the results.
            IOHelper.populateTableWithResults(schedulingDataTable, results);
        }
    }

    /**
     * This method is used to set up a table listener on the scheduling data table.
     * Listener will listen for a selection, and fill in the selected appointment data to the corresponding text fields.
     * *** LAMBDA EXPRESSION*** This lambda expression simplifies the code while setting up much-needed functionality.
     */
    private void setTableListener() {

        // Sets up listener to populate the selected appointment data into the text fields when an appointment is selected.
        schedulingDataTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> fillInSelectedAppointmentData());
    }

    /**
     * This method is used to toggle radio buttons.
     * Adds the scenes radio buttons to an observable list and send them to the IOHelper class to be toggled.
     * Repopulates the scheduling data based on the toggled radio button.
     * @param e Action event of which the radio toggling occurred.
     */
    @FXML
    private void radioButtonSelected(ActionEvent e){

        // List to the store the radio buttons in.
        ObservableList<RadioButton> radioButtons = FXCollections.observableArrayList();

        // Add the month radio button.
        radioButtons.add(byMonthToggle);

        // Add the week radio button.
        radioButtons.add(byWeekToggle);

        // Add the all radio button.
        radioButtons.add(allToggle);

        // Uses the IOHelper to toggle the radio buttons.
        IOHelper.toggleRadioButtons((RadioButton) e.getSource(), radioButtons);

        // Repopulates the Scheduling data table.
        populateSchedulingData();
    }

    /**
     * This method is used to populate all initially populated combo boxes.
     * Calls methods to populate combo boxes.
     *** ADD NEW COMBO BOX POPULATION METHODS HERE.***
     */
    private void populateComboBoxes() {

        // Call to populate the contact combo box.
        populateContactComboBox();

        // Call to populate the type combo box.
        populateTypeComboBox();

        // Call to populate the customer combo box.
        populateCustomerComboBox();

        // Call to populate the user combo box.
        populateUserComboBox();

        // Call to populate the start time combo box.
        populateTimeComboBox(startTime);

        // Call to populate the end time combo box.
        populateTimeComboBox(endTime);
    }

    /**
     * This method is used to populate the Contact combo box.
     * Gets all contacts from the database using the contact helper class.
     * populates contact combo box with results.
     */
    private void populateContactComboBox() {

        // Gets all the available contact combo box choices from the database using the Contact helper class.
        ResultSet results = ContactHelper.getAllContacts();

        // Populates the contact combo box with the results.
        IOHelper.populateComboBoxWithResults(appointmentContact, results);
    }

    /**
     * This method is used to populate the type combo box.
     * Gets all type from the database static class.
     * populates contact combo box with results.
     */
    private void populateTypeComboBox() {

        // For each element in the types list.
        for(String type: Database.getInstance().getAppointmentTypes()) {

            // Populates the types' combo box with the results.
            appointmentType.getItems().add(type);
        }
    }

    /**
     * This method is used to populate the Customer combo box.
     * Gets all customers from the database using the customer helper class.
     * populates customer combo box with results.
     */
    private void populateCustomerComboBox() {

        // Gets all the available customer combo box choices from the database using the customer helper class.
        ResultSet results = CustomerHelper.getAllCustomers();

        // Populates the customer combo box with the results.
        IOHelper.populateComboBoxWithResults(customerID, results);
    }

    /**
     * This method is used to populate the user combo box.
     * Gets all user from the database using the user helper class.
     * populates contact combo box with results.
     */
    private void populateUserComboBox() {

        // Gets all the available user combo box choices from the database using the user helper class.
        ResultSet results = UserHelper.getAllUsers();

        // Populates the user combo box with the results.
        IOHelper.populateComboBoxWithResults(userID, results);
    }

    /**
     * This method is used to populate the Times combo boxes.
     * Generates the times.
     * populates contact combo box with results as it goes along.
     * @param times The time combo box to be populated.
     */
    private void populateTimeComboBox(ComboBox<String> times) {

        // This is the int for the 10s place for the hours.
        int hourTens = 0;

        // This is the int for the 1s place for the hours.
        int hourOnes = 0;

        // For each hour in the day.
        for(int hours = 0; hours < 24; hours++){

            // Add the hour.
            times.getItems().add(String.valueOf(hourTens) + hourOnes + ":00:00");

            // For each 15 minute interval.
            for(int minutes = 15; minutes < 60; minutes = minutes + 15){

                // Add the incremented time.
                times.getItems().add(String.valueOf(hourTens) + hourOnes + ":" + minutes + ":00");
            }

            // If 1s place is 9 Resets the ones and increments the 10s place.
            if(hourOnes == 9){

                // Increments the 10s place.
                hourTens++;

                // Resets the 1s place.
                hourOnes = 0;
            }
            else{

                // If not increment 1s place.
                hourOnes++;
            }
        }
    }

    /**
     * This method is used to clear the Appointment data fields.
     * Calls a method to reset the combo boxes.
     * Sets all the text and text fields to an empty string.
     */
    private void clearAppointmentDataFields() {

        // Set appointment ID Text field to an empty string.
        appointmentID.setText("");

        // Set appointment title Text field to an empty string.
        appointmentTitle.setText("");

        // Set appointment title error Text to an empty string.
        appointmentTitleError.setText("");

        // Set appointment description Text field to an empty string.
        appointmentDescription.setText("");

        // Set appointment description error Text to an empty string.
        appointmentDescriptionError.setText("");

        // Resets the combo boxes.
        resetComboBoxes();

        // Empties the date pickers.
        emptyDatePickers();

        // Set appointment location Text field to an empty string.
        appointmentLocation.setText("");

        // Set appointment location error Text to an empty string.
        appointmentLocationError.setText("");
    }

    /**
     * This method resets the combo boxes.
     * Calls IOHelper class to clear combo boxes.
     * Resets the corresponding error text.
     */
    private void resetComboBoxes() {

        // Uses IOHelper class to clear the appointment contact combo box.
        IOHelper.clearComboBox(appointmentContact);

        // Sets the appointment contact error Text to an empty string.
        appointmentContactError.setText("");

        // Uses IOHelper class to clear the appointment type combo box.
        IOHelper.clearComboBox(appointmentType);

        // Sets the appointment type error Text to an empty string.
        appointmentTypeError.setText("");

        // Uses IOHelper class to clear the customer ID combo box.
        IOHelper.clearComboBox(customerID);

        // Sets the customer ID error Text to an empty string.
        customerIDError.setText("");

        // Uses IOHelper class to clear the User ID combo box.
        IOHelper.clearComboBox(userID);

        // Sets the User ID error Text to an empty string.
        userIDError.setText("");

        // Uses IOHelper class to clear the start time combo box.
        IOHelper.clearComboBox(startTime);

        // Sets the start time error Text to an empty string.
        startTimeError.setText("");

        // Uses IOHelper class to clear the end time combo box.
        IOHelper.clearComboBox(endTime);

        // Sets the end time error Text to an empty string.
        endTimeError.setText("");
    }

    /**
     * This method empties the date pickers.
     * Sets the values to null.
     */
    private void emptyDatePickers() {

        // Sets the start date picker to null.
        startDate.setValue(null);

        //Sets the end date picker to null.
        endDate.setValue(null);
    }

    /**
     * This method is used to fill in the selected appointment data to the appointment data fields using the IO Helper class.
     * Checks if the scheduling table selection is empty and cancels the operation if true.
     * Clears all appointment data fields.
     */
    private void fillInSelectedAppointmentData() {

        // Checks if the scheduling data table has a selection.
        if(schedulingDataTable.getSelectionModel().isEmpty()){

            // Cancels the operation if true.
            return;
        }

        // Clears all the appointment data fields to be populated with selected appointment data.
        clearAppointmentDataFields();

        // Sets the appointment ID to the selected appointments.
        appointmentID.setText(IOHelper.getValueFromRowColumn(schedulingDataTable, "Appointment_ID"));

        // Sets the appointment title to the selected appointments.
        appointmentTitle.setText(IOHelper.getValueFromRowColumn(schedulingDataTable, "Title"));

        // Sets the appointment description to the selected appointments.
        appointmentDescription.setText(IOHelper.getValueFromRowColumn(schedulingDataTable, "Description"));

        // Sets the date pickers to the selected appointments.
        fillDatePickers();

        // Fills in the combo boxes with selected appointments' data.
        fillInComboBoxes();

        // Sets the appointment location to the selected appointments.
        appointmentLocation.setText(IOHelper.getValueFromRowColumn(schedulingDataTable, "Location"));
    }

    /**
     * This method is used to fill in appointment data to the combo boxes.
     * Uses the IOHelper class to set the appointments' data into the corresponding combo boxes.
     *** ADD NEW COMBO BOX FILL METHODS HERE ***
     */
    private void fillInComboBoxes() {

        // Uses the IO Helper class to set the appointment contact combo box with customer data.
        IOHelper.setComboBox(appointmentContact,  appointmentContact.getItems().indexOf(IOHelper.getValueFromRowColumn(schedulingDataTable, "Contact_Name")));

        // Uses the IO Helper class to set the appointment type combo box with customer data.
        IOHelper.setComboBox(appointmentType,  appointmentType.getItems().indexOf(IOHelper.getValueFromRowColumn(schedulingDataTable, "Type")));

        // Uses the IO Helper class to set the appointment ID combo box with customer data.
        IOHelper.setComboBox(customerID,  customerID.getItems().indexOf(IOHelper.getValueFromRowColumn(schedulingDataTable, "Customer_ID")));

        // Uses the IO Helper class to set the User ID combo box with customer data.
        IOHelper.setComboBox(userID,  userID.getItems().indexOf(UserHelper.getUserIDFromAppointment(appointmentID.getText())));

        // Uses the IO Helper class to set the start time combo box with customer data.
        IOHelper.setComboBox(startTime,  startTime.getItems().indexOf(IOHelper.getValueFromRowColumn(schedulingDataTable, "Start").split("\\s")[1].replaceAll("\\.0", "")));

        // Uses the IO Helper class to set the end time combo box with customer data.
        IOHelper.setComboBox(endTime,  endTime.getItems().indexOf(IOHelper.getValueFromRowColumn(schedulingDataTable, "End").split("\\s")[1].replaceAll("\\.0", "")));
    }

    /**
     * This method is used to fill in the date pickers using the selected appointments' data.
     * Sets the date format.
     * Sets the date format to use the default locale.
     * Sets the start and end date values.
     */
    private void fillDatePickers() {

        // Sets the date format.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Sets the formatter to use the default locale.
        formatter = formatter.withLocale(Locale.getDefault());

        // Sets the local start date.
        startDate.setValue(LocalDate.parse(IOHelper.getValueFromRowColumn(schedulingDataTable, "Start").split("\\s")[0], formatter));

        // Sets the local end date.
        endDate.setValue(LocalDate.parse(IOHelper.getValueFromRowColumn(schedulingDataTable, "End").split("\\s")[0], formatter));
    }

    /**
     * This method is used to get the hypothetical next ID from the database.
     * Uses the Appointment Helper class to get the next ID.
     * @return String representation of the next ID.
     */
    private String nextId() {

        // Returns the hypothetical next ID from the database.
        return AppointmentHelper.generateNextAppointmentId();
    }

    /**
     * This method is used to set the next appointment id.
     * Uses the given string to set the appointment ID text field, includes message about it being generated.
     * @param nextId The string representation of the next hypothetical appointment ID.
     */
    private void setNextAppointmentId(String nextId) {

        // Sets the appointments ID text field to the hypothetical next appointment ID with a message about it being generated.
        appointmentID.setText(nextId + " (" + LanguageHelper.getResourceBundle().getString("textField.appointments.prompt.id") + ")");
    }

    /**
     * This method is used to focus on the appointment title field.
     * Uses IOHelper to focus on the appointment title field.
     */
    private void focusOnAppointmentTitle() {

        // Focuses on appointment title text field.
        IOHelper.focusOnElement(appointmentTitle);
    }

    /**
     * This method is used to validate the user input data for the appointment, and display error where appropriate.
     * Sets up a boolean isValid to keep track of validation throughout method.
     * Calls individual validation methods from the IOHelper class.
     * sets isValid to false if any validation fails.
     * Returns true if all data is valid.
     * @return The boolean representation of whether or no all data was validated successfully.
     */
    private boolean validateAppointmentData() {

        // Sets up the boolean used to keep track of the validation success status.
        boolean isValid = true;

        // Uses IOHelper class to check if the appointment title is a valid string.
        if(!IOHelper.isStringValid(appointmentTitle, appointmentTitleError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.title"))){

            // Sets the isValid boolean to false if the appointment title data is not valid.
            isValid = false;
        }

        // Uses IOHelper class to check if the appointment description is a valid string.
        if(!IOHelper.isStringValid(appointmentDescription, appointmentDescriptionError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.description"))){

            // Sets the isValid boolean to false if the appointment description data is not valid.
            isValid = false;
        }

        // Uses IOHelper class to check if the appointment location is a valid string.
        if(!IOHelper.isStringValid(appointmentLocation, appointmentLocationError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.location"))){

            // Sets the isValid boolean to false if the appointment location data is not valid.
            isValid = false;
        }

        // Uses IOHelper class to check if the contact is selected.
        if(IOHelper.isComboBoxUnselected(appointmentContact, appointmentContactError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.contact"))){

            // Sets the isValid boolean to false if the contact is not selected.
            isValid = false;
        }

        // Uses IOHelper class to check if the type is selected.
        if(IOHelper.isComboBoxUnselected(appointmentType, appointmentTypeError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.type"))){

            // Sets the isValid boolean to false if the type is not selected.
            isValid = false;
        }

        // Uses IOHelper class to check if the start time is selected.
        if(IOHelper.isComboBoxUnselected(startTime, startTimeError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.startTime"))){

            // Sets the isValid boolean to false if the start time is not selected.
            isValid = false;
        }

        // Uses IOHelper class to check if the end time is selected.
        if(IOHelper.isComboBoxUnselected(endTime, endTimeError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.endTime"))){

            // Sets the isValid boolean to false if the end time is not selected.
            isValid = false;
        }

        // Uses the Appointment helper to check if the appointment start time is during business hours.
        if(AppointmentHelper.isNotDuringBusinessHours(startTime, startTimeError,
                LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.startTime"))){

            // Sets the isValid boolean to false if the start time is not during business hours.
            isValid = false;
        }

        // Uses the Appointment helper to check if the appointment end time is during business hours.
        if(AppointmentHelper.isNotDuringBusinessHours(endTime, endTimeError,
                LanguageHelper.getResourceBundle().getString("appointmentHelper.errorMessage.times.endTime"))){

            // Sets the isValid boolean to false if the end time is not during business hours.
            isValid = false;
        }

        // Uses the Appointment helper to check if the appointment is during another appointment.
        if(AppointmentHelper.isTimeDuringScheduledAppointment(appointmentID.getText(), customerID.getSelectionModel().getSelectedItem(),
                startDate.getValue(), endDate.getValue(), startTime, endTime, startTimeError, endTimeError,
                "Start","End")){

            // Sets the isValid boolean to false if the end time is during another appointment.
            isValid = false;
        }

        // Uses IOHelper class to check if the customer ID is selected.
        if(IOHelper.isComboBoxUnselected(customerID, customerIDError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.customerID"))){

            // Sets the isValid boolean to false if the customer ID is not selected.
            isValid = false;
        }

        // Uses IOHelper class to check if the User ID is selected.
        if(IOHelper.isComboBoxUnselected(userID, userIDError,
                LanguageHelper.getResourceBundle().getString("scheduling.controller.string.userID"))){

            // Sets the isValid boolean to false if the User ID is not selected.
            isValid = false;
        }

        // returns is valid.
        return isValid;
    }
}
