package emil.find_course.common.exception;

public class EmailConfirmException extends RuntimeException {

    public EmailConfirmException(String message) {
        super(message);
    }

    public EmailConfirmException(String message, Throwable cause) {
        super(message, cause);
    }
}