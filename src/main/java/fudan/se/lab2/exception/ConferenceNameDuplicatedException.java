package fudan.se.lab2.exception;

/**
 * @author YHT
 */
public class ConferenceNameDuplicatedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConferenceNameDuplicatedException(String fullName) {
        super("Conference '" + fullName + "' was already applying.");
    }
}

