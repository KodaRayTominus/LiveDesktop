import Classes.*;

// Javafx import statements needed to run.
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

// Java import statements needed to run.
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This class is for handling the event and functionality for the Login Window pane.
 * This class links to LoginWindowPane.FXML using @FXML,OnAction, fx:id and fx:controller.
 * @author Dakota Ray Tominus
 */
public class LoginWindowPaneController {

    // The root object of the scene.
    @FXML
    Parent root;

    // The text field used to input username.
    @FXML
    TextField userIdField;

    // The text used for username related errors.
    @FXML
    Text userIdErrorText;

    // The text field used to input user password.
    @FXML
    TextField userPasswordField;

    // The text used for user password and login related errors.
    @FXML
    Text userPasswordErrorText;

    // The zone ID of the current users' system.
    @FXML
    Label zoneIdLabel;

    /**
     * This method runs everytime the pane is loaded.
     * Sets the zone ID.
     */
    @FXML
    private void initialize(){

        // Sets the zone ID.
        setZoneId();
    }

    /**
     * This method is used to log in.
     * Writes attempt to a file to be logged.
     * Checks if user has upcoming appointments and alerts to the status.
     * Catches failed IOException and prints the stack trace.
     */
    @FXML
    private void login(){

        // Checks if the username and password match using the User Helper class.
        if(UserHelper.checkUserNameToPassword(userIdField.getText(), userIdErrorText, userPasswordField.getText(), userPasswordErrorText)){

            // Log successful login attempt.
            IOHelper.logSuccessfulLogin(userIdField.getText());

            // Try to catch IOException.
            try{

                // Checks for user upcoming appointments and alerts the user to the status.
                checkAndAlertForAppointment();

                // Open new scene
                SceneManager.openScene(root, new Scene(FXMLLoader.load(getClass().getResource("/Scenes/CustomerDataWindowPane.fxml"), LanguageHelper.getResourceBundle())));
            }
            catch (IOException e){

                // Prints caught exception if Customer Data window pane fails to load.
                e.printStackTrace();
            }
        }
        else{

            // If failed to log in, log attempt.
            IOHelper.logFailedLogin(userIdField.getText());
        }
    }

    /**
     * This method is used to set the zone ID to the zone ID text.
     * Uses the systems zoned date time to get zone ID.
     */
    private void setZoneId() {

        // Gets and sets the current zone ID for the user.
        zoneIdLabel.setText(ZoneId.from(ZonedDateTime.now()).toString());
    }

    /**
     * This method is used to check for upcoming appointments and alert the user to the status.
     * Creates pop up alert with appointment information.
     * Populates alert with appointment data if within the given time frame.
     * Alert will wait for user response.
     */
    private void checkAndAlertForAppointment() {

        // Creates a new alert.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        // Sets the title on the new alert.
        alert.setTitle(LanguageHelper.getResourceBundle().getString("alert.login.title.text"));

        // Checks if the user has an appointment within the time period using the User Helper class.
        if(UserHelper.hasAppointmentWithin(15)){

            // Sets the header on the alert.
            alert.setHeaderText(LanguageHelper.getResourceBundle().getString("alert.login.header.affirmative"));

            // Uses the User helper class to set the alert data from the users appointments in the database.
            UserHelper.setUpAlertForUserNextAppointment(alert);
        }
        else{

            // If no appointment found, sets the header on the alert.
            alert.setHeaderText(LanguageHelper.getResourceBundle().getString("alert.login.header.negative"));

            // Sets the message of the alert.
            alert.setContentText(LanguageHelper.getResourceBundle().getString("alert.login.content.text"));
        }

        // Creates a new alert and waits.
        alert.showAndWait();
    }
}
