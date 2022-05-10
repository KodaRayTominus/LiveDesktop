import Classes.AppointmentHelper;
import Classes.Database;
import Classes.LanguageHelper;

// Javafx import statements needed to run.
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

// Java import statements needed to run.
import java.text.DateFormatSymbols;

/**
 * This class is for handling the event and functionality for the Appointment Report Window pane.
 * This class links to AppointmentReportWindowPaneController.FXML using @FXML, fx:id and fx:controller.
 * @author Dakota Ray Tominus
 */
public class AppointmentReportWindowPaneController {

    // Text area for the report to be shown in.
    @FXML
    TextArea textArea;
    
    /**
     * This method runs everytime the pane is loaded.
     */
    @FXML
    private void initialize(){

        // Populates the report data.
        populateReportData();
    }

    /**
     * This Method is used to populate the report data.
     * First the Data will print by month.
     * Then the data will print by type.
     */
    private void populateReportData() {

        // populates the Monthly report.
        populateAppointmentsByMonth();

        // Populates the Type report.
        populateAppointmentsByType();
    }

    /**
     * This Method populates the report with the data, by month.
     * This method uses local monthly symbols, which are capitalized.
     */
    private void populateAppointmentsByMonth() {

        // Prints the Month header to the report.
        printLineToReport(LanguageHelper.getResourceBundle().getString("text.appointmentReport.text.month"));

        // For loop for populating the report, starts at one to coincide with dates.
        for(int i = 1; i <=12; i++){

            // Gets the appointment count by monthly integer.
            int appointmentCount = AppointmentHelper.getAppointmentCountByMonth(i);

            // Prints the monthly appointment count with the local monthly corresponding symbol, capitalized.
            printLineToReport(System.getProperty("line.separator") + DateFormatSymbols.getInstance().getMonths()[i - 1].replaceFirst(String.valueOf(DateFormatSymbols.getInstance().getMonths()[i - 1].charAt(0)), String.valueOf(DateFormatSymbols.getInstance().getMonths()[i - 1].charAt(0)).toUpperCase()) + " - " + appointmentCount);
        }

        // Prints a new line to separate from further data.
        printLineToReport(System.getProperty("line.separator"));
    }

    /**
     * This Method populates the report with the data, by type.
     */
    private void populateAppointmentsByType() {

        // Prints the Type header to the report.
        printLineToReport(System.getProperty("line.separator") + LanguageHelper.getResourceBundle().getString("text.appointmentReport.text.type"));

        // Foreach loop for populating the report.
        for (String type: Database.getInstance().getAppointmentTypes()) {

            // Prints the Type appointments count.
            printLineToReport(System.getProperty("line.separator") + type + " - " + AppointmentHelper.getAppointmentCountByType(type));
        }
    }

    /**
     * This Method is used to print a line to the report text area.
     * @param line The line to be added to the text area.
     */
    private void printLineToReport(String line) {

        // Appends a new line onto the text area.
        textArea.appendText(line);
    }
}
