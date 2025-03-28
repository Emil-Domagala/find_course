package emil.find_course.exceptions;

import lombok.Getter;

@Getter
public class FieldValidationException extends RuntimeException {
    private final String fieldError;

    public FieldValidationException(String fieldError, String message) {
        super(message);
        this.fieldError = fieldError;
    }
}
