import Classes.AppointmentHelper;
import Classes.ContactHelper;
import Classes.IOHelper;

// Javafx import statements needed to run.
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

// Java import statements needed to run.
import java.sql.ResultSet;
import java.util.Map;

/**
 * This class is for handling the event and functionality for the Contact Schedule Report Window pane.
 * This class links to ContactScheduleReportWindowPaneController.FXML using @FXML, OnAction, fx:id and fx:controller.
 * @author Dakota Ray Tominus
 */
public class ContactScheduleReportWindowPaneController {

    // The combo box used for selecting which contacts schedule is displayed.
    @FXML
    ComboBox<String> contactComboBox;

    // The Table used to display the selected contacts schedule.
    @FXML
    TableView<Map<String, String>> contactDataTable;
    
    /**
     * This method runs everytime the pane is loaded.
     * Populates the contact combo box.
     */
    @FXML
    private void initialize(){

        // Populates the contact combo box with contacts from the database.
        populateContactComboBox();
    }

    /**
     * This method is used to populate the contact combo box.
     * Uses the Contact helper class to access the database and get all contacts.
     * Populates the results into the combo box.
     */
    private void populateContactComboBox() {

        // Gets all contacts using the Contact Helper class.
        ResultSet results = ContactHelper.getAllContacts();

        // Populates the combo box with the results using the IOHelper class.
        IOHelper.populateComboBoxWithResults(contactComboBox, results);
    }

    /**
     * This method is used to populate the table with the selected contacts schedule.
     * Clears the table of data.
     * Gets the contacts appointment schedule using the Appointment helper class.
     * Populates the table with the results.
     */
    @FXML
    private void populateTable(){

        // Clears the table of the current data.
        contactDataTable.getItems().clear();

        // Gets all the selected contacts appointments.
        ResultSet results = AppointmentHelper.getAllAppointmentsForContact(contactComboBox.getSelectionModel().getSelectedItem());

        // Uses the results to fill the table.
        IOHelper.populateTableWithResults(contactDataTable, results);
    }
}
