package emil.find_course.course.section.dto.request.validator;

import java.util.UUID;

import emil.find_course.course.section.dto.request.SectionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SectionRequestValidator implements ConstraintValidator<ValidSectionRequest, SectionRequest> {

    @Override
    public boolean isValid(SectionRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull if needed
        }
        context.disableDefaultConstraintViolation();

        UUID id = value.getId();
        String tempId = value.getTempId();
        String title = value.getTitle();
        String description = value.getDescription();

        if (id == null && tempId == null) {
            context.buildConstraintViolationWithTemplate("Either id or tempId must be provided")
                    .addConstraintViolation();
            return false;
        }
        if (tempId != null && (title == null || (title == null && description == null))) {
            context.buildConstraintViolationWithTemplate(
                    "If tempId is provided, title must be present and either content or videoUrl must be provided")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
