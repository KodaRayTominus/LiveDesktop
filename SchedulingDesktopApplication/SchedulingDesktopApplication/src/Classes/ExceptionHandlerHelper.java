package Classes;

import javax.swing.*;

public class ExceptionHandlerHelper extends Helper{

    /**
     * String representation of the class type
     */
    String string = "Exception Handler Helper";

    /**
     *  Empty constructor for instantiating the class, and it's inherited properties
     */
    public ExceptionHandlerHelper(){

        super();
    }

    /**
     * This method is used to process the exception adn prompt the user to seek help.
     * @param thread The thread the exception was thrown on
     * @param throwable The exception that was thrown
     */
    @Override
    public void throwException(Thread thread, Throwable throwable){

        JOptionPane.showMessageDialog(null, "Caught Unchecked exception in " + this + ". Please contact IT.");
    }


    /**
     * This method is for getting the string representation of this class
     * @return the string representation for the class
     */
    @Override
    public String toString(){

        return string;
    }

    public void handleException(){

        JOptionPane.showMessageDialog(null, "Caught Unchecked exception in " + this + ". Please contact IT.");
    }
}
