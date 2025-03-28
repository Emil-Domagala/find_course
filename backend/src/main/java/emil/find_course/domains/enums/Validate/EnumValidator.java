package emil.find_course.domains.enums.Validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Class<Enum> enumClassSpecific = (Class<Enum>) enumClass;
            Enum.valueOf(enumClassSpecific, value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
