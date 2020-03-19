package fudan.se.lab2.exception;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException (String username) {
        super("Username '" + username + "' got wrong password");
    }
}
