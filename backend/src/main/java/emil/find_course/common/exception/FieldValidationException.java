package emil.find_course.common.exception;

import lombok.Getter;

@Getter
public class FieldValidationException extends RuntimeException {
    private final String fieldError;

    public FieldValidationException(String fieldError, String message) {
        super(message);
        this.fieldError = fieldError;
    }
}
