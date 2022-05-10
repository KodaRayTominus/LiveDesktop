package Classes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 */
public class SceneManager {

    /**
     *
     * @param root root object
     * @param scene scene to be opened
     */
    public static void openScene(Parent root, Scene scene) {

        // Gets the active stage from the node.
        Stage stage = (Stage) root.getScene().getWindow();

        // Sets the new scene in the stage.
        stage.setScene(scene);

        // Center the window
        stage.centerOnScreen();

        // Opens the new scene in the same window.
        stage.show();
    }

    /**
     *
     * @param scene scene to be opened
     */
    public static void openReport(Scene scene) {

        // Gets a new stage.
        Stage stage = new Stage();

        // Sets the new scene in the stage.
        stage.setScene(scene);

        // Opens the new scene in a new window.
        stage.show();
    }
}
