package emil.find_course.common.exception;

public class InvalidRefreshTokenException extends JwtAuthException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }

    public InvalidRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}