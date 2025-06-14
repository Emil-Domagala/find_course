package emil.find_course.course.chapter.dto.request.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChapterRequestValidator.class)
@Documented
public @interface ValidChapterRequest {
    String message() default "Invalid ChapterRequest";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}