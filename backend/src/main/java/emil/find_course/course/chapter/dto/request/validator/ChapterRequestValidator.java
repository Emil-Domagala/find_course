package emil.find_course.course.chapter.dto.request.validator;

import java.util.UUID;

import emil.find_course.course.chapter.dto.request.ChapterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChapterRequestValidator implements ConstraintValidator<ValidChapterRequest, ChapterRequest> {
    public boolean isValid(ChapterRequest chapterRequest, ConstraintValidatorContext context) {
        if(chapterRequest == null){
            return true; // @NotNull if needed
        }

        UUID id = chapterRequest.getId();
        String tempId = chapterRequest.getTempId();
        String title = chapterRequest.getTitle();
        String content = chapterRequest.getContent();
        String videoUrl = chapterRequest.getVideoUrl();
     
        context.disableDefaultConstraintViolation();

        if (id == null && tempId == null) {
            context.buildConstraintViolationWithTemplate("Either id or tempId must be provided")
                    .addConstraintViolation();
            return false;
        }
        
        if (tempId != null && (title == null || (content == null && videoUrl == null))) {
            context.buildConstraintViolationWithTemplate(
                    "If tempId is provided, title must be present and either content or videoUrl must be provided")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
