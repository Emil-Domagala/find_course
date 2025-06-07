package emil.find_course.common.exception;

public class EmailFilterException extends RuntimeException {

    public EmailFilterException(String message) {
        super(message);
    }

    public EmailFilterException(String message, Throwable cause) {
        super(message, cause);
    }
}