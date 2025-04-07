package emil.find_course.exceptions;

public class EmailConfirmException extends RuntimeException {

    public EmailConfirmException(String message) {
        super(message);
    }

    public EmailConfirmException(String message, Throwable cause) {
        super(message, cause);
    }
}