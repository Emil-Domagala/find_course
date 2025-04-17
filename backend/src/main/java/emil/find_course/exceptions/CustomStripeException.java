package emil.find_course.exceptions;


public class CustomStripeException extends RuntimeException {
    public CustomStripeException(String message) {
        super(message);
    }

    public CustomStripeException(String message, Throwable cause) {
        super(message, cause);
    }
}
