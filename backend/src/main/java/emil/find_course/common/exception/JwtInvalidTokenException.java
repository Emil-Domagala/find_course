package emil.find_course.common.exception;

public class JwtInvalidTokenException extends JwtAuthException {
    public JwtInvalidTokenException(String message) {
        super(message);
    }

    public JwtInvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
