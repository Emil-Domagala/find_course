package emil.find_course.domains.requestDto.course;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChapterRequest {

    @Nullable
    private UUID id;

    @Nullable
    private String tempId;

    @NotBlank
    @Positive(message = "Price must be a positive value")
    private Integer position;

    @Nullable
    @Size(min = 3, max = 100, message = "Chapter title must be between {min} and {max} characters")
    private String title;

    @Nullable
    @Size(min = 3, max = 1000, message = "Chapter content must be between {min} and {max} characters")
    private String content;

    @Nullable
    private MultipartFile video;

    @Valid
    private boolean isValid() {
        if (id == null && tempId == null) {
            return false;
        }
        return true;
    }

}
