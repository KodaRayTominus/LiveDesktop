package Main;
import Classes.Database;
import Classes.LanguageHelper;

// Javafx import statements needed to run.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main class that runs the application.
 * ***JAVADOC*** Can be found in /JavaDoc OR From the program Menu > Help > JavaDoc.
 * @author Dakota Ray Tominus
 */
public class SchedulingDesktopApplication extends Application {

    /**
     * This method is called to launch to the program.
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Sets up the language.
        LanguageHelper.setLanguage();

        // Starts the database connection.
        Database.getInstance().startConnection();

        // Launches the main program.
        launch(args);
    }

    /**
     * This method is used to start the program.
     * This sets up the Scheduler for use.
     * This calls the first scene and window pane and shows it.
     * @param primaryStage The primary stage to be used to scenes.
     * @throws Exception FXML file cannot be found.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

        // Sets the window pane title.
        primaryStage.setTitle(LanguageHelper.getResourceBundle().getString("title.login.title"));

        // Loads the first scene.
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Scenes/LoginWindowPane.fxml"),  LanguageHelper.getResourceBundle())));

        // Centers the window pane.
        primaryStage.centerOnScreen();

        // Shows the first scene.
        primaryStage.show();
    }
}
