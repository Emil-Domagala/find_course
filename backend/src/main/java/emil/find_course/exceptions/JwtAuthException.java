package emil.find_course.exceptions;

public class JwtAuthException extends RuntimeException {
    public JwtAuthException(String message) {
        super(message);
    }

    public JwtAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
