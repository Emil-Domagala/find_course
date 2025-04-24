package emil.find_course.domains.requestDto.course;

import java.util.List;
import java.util.UUID;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SectionRequest {
    @Nullable
    private UUID id;

    @Nullable
    private String tempId;

    @NotBlank
    @Positive(message = "Price must be a positive value")
    private Integer position;

    @Nullable
    @Size(min = 3, max = 100, message = "Section title must be between {min} and {max} characters")
    private String title;

    @Nullable
    @Size(min = 3, max = 500, message = "Section description must be between {min} and {max} characters")
    private String description;

    @Nullable
    private List<ChapterRequest> chapters;

    @Valid
    private boolean isValid() {
        if (id == null && tempId == null) {
            return false;
        }
        return true;
    }
}
