package Classes;

public class LoginTest {

    private String testUserName = "John";

    private String testPassword = "password123";


    private void testLogin() {

    // Checks if the username and password match using the User Helper class.
        if(UserHelper.checkUserNameToPassword(testUserName,null,testPassword,null)){

        // Log successful login attempt.
        IOHelper.logSuccessfulLogin(testUserName);
        } else{

            // If failed to log in, log attempt.
            IOHelper.logFailedLogin(testUserName);
        }
    }
}
