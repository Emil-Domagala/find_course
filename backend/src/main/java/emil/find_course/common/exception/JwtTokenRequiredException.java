package emil.find_course.common.exception;

public class JwtTokenRequiredException extends JwtAuthException {
    public JwtTokenRequiredException(String message) {
        super(message);
    }

    public JwtTokenRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
