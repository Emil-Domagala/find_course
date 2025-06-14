package emil.find_course.course.section.dto.request.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SectionRequestValidator.class)
@Documented
public @interface ValidSectionRequest {
    String message() default "SectionRequest is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}