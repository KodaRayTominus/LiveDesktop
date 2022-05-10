package Classes;

import javax.swing.*;

public class Helper {

    /**
     * String representation of the class type
     */
    String string = "Basic/Unnamed Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public Helper(){

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {

                throwException(t, e);
            }
        });
    }

    /**
     * This method is used to process the exception adn prompt the user to seek help.
     * @param thread The thread the exception was thrown on
     * @param throwable The exception that was thrown
     */
    public void throwException(Thread thread, Throwable throwable){

        //Throw popup message
        JOptionPane.showMessageDialog(null, "Caught Unchecked exception in " + this + ". Please contact IT.");
    }


    /**
     * This method is for getting the string representation of this class
     * @return the string representation for the class
     */
    public String toString(){

        return string;
    }
}
