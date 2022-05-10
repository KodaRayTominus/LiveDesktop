package Classes;

// Java import statements needed to run.
import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * This class is for handling the language functionality.
 * @author Dakota Ray Tominus
 */
public class LanguageHelper extends Helper {

    /**
     * String representation of the class type
     */
    String string = "Language Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public LanguageHelper(){

        super();
    }

    /**
     * This method is used to process the exception adn prompt the user to seek help.
     * @param thread The thread the exception was thrown on
     * @param throwable The exception that was thrown
     */
    @Override
    public void throwException(Thread thread, Throwable throwable){

        JOptionPane.showMessageDialog(null, "Caught Unchecked exception in " + toString() + ". Please contact IT.");
    }


    /**
     * This method is for getting the string representation of this class
     * @return the string representation for the class
     */
    @Override
    public String toString(){

        return string;
    }

    // The resource bundle for the entire program.
    private static ResourceBundle resourceBundle;

    /**
     * This method is used to set the default language based on the users' system language.
     * Sets the language and sets up the associated resource bundle for that language.
     */
    public static void setLanguage() {

        // Set the default language to the users' system language.
        Locale.setDefault(new Locale(System.getProperty("user.language")));

        // Set the Resource bundle here.
        resourceBundle = ResourceBundle.getBundle("Languages/rb", Locale.getDefault());
    }

    /**
     * This method is used to get the resource bundle.
     * Returns the resource bundle for the program.
     * @return The resource bundle for the program.
     */
    public static ResourceBundle getResourceBundle(){

        // Returns the resource bundle for use.
        return resourceBundle;
    }
}
