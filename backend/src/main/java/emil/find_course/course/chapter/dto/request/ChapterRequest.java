package emil.find_course.course.chapter.dto.request;

import java.util.UUID;

import org.hibernate.validator.constraints.URL;

import emil.find_course.course.chapter.dto.request.validator.ValidChapterRequest;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@ValidChapterRequest
@Data
@Builder(toBuilder = true)
public class ChapterRequest {

    @Nullable
    private UUID id;

    @Nullable
    private String tempId;

    @Nullable
    @Size(min = 3, max = 100, message = "Chapter title must be between {min} and {max} characters")
    private String title;

    @Nullable
    @Size(min = 3, max = 1000, message = "Chapter content must be between {min} and {max} characters")
    private String content;

    @Nullable
    @URL(message = "Invalid video URL")
    private String videoUrl;

}
