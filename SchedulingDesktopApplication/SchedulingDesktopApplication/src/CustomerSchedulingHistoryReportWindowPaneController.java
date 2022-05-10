import Classes.AppointmentHelper;
import Classes.CustomerHelper;
import Classes.IOHelper;

// Javafx import statements needed to run.
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

// Java import statements needed to run.
import java.sql.ResultSet;
import java.util.Map;

/**
 * This class is for handling the event and functionality for the Customer Scheduling History Report Window pane.
 * This class links to CustomerSchedulingHistoryReportWindowPane.FXML using @FXML, fx:id and fx:controller.
 * @author Dakota Ray Tominus
 */
public class CustomerSchedulingHistoryReportWindowPaneController {

    // The combo box used for selecting which customers appointment history is displayed.
    @FXML
    ComboBox<String> customerComboBox;

    // The Table used to display the selected customers appointment history.
    @FXML
    TableView<Map<String, String>> customerDataTable;

    /**
     * This method runs everytime the pane is loaded.
     * Populates the contact combo box.
     */
    @FXML
    private void initialize(){

        // Populates the customer combo box with customers from the database.
        populateCustomerComboBox();
    }

    /**
     * This method is used to populate the customer combo box.
     * Uses the customer helper class to access the database and get all customers.
     * Populates the results into the combo box.
     */
    private void populateCustomerComboBox() {

        // Gets all customers using the customer Helper class.
        ResultSet results = CustomerHelper.getAllCustomers();

        // Populates the combo box with the results using the IOHelper class.
        IOHelper.populateComboBoxWithResults(customerComboBox, results);
    }

    /**
     * This method is used to populate the table with the selected customers appointments.
     * Clears the table of data.
     * Gets the customers appointments using the Appointment helper class.
     * Populates the table with the results.
     */
    @FXML
    private void populateTable(){

        // Clears the table of the current data.
        customerDataTable.getItems().clear();

        // Gets all the selected customers appointments.
        ResultSet results = AppointmentHelper.getAllAppointmentsForCustomer(customerComboBox.getSelectionModel().getSelectedItem());

        // Uses the results to fill the table.
        IOHelper.populateTableWithResults(customerDataTable, results);
    }
}
