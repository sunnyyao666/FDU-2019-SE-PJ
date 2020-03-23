package fudan.se.lab2.exception;

/**
 * @author YHT
 */
public class ConferenceNameDuplicatedExecption extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConferenceNameDuplicatedExecption(String fullName) {
        super("Conference '" + fullName + "' was already applying.");
    }
}

