package emil.find_course.course.section.dto.request;

import java.util.List;
import java.util.UUID;

import emil.find_course.course.chapter.dto.request.ChapterRequest;
import emil.find_course.course.section.dto.request.validator.ValidSectionRequest;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@ValidSectionRequest
public class SectionRequest {
    @Nullable
    private UUID id;

    @Nullable
    private String tempId;

    @Nullable
    @Size(min = 3, max = 100, message = "Section title must be between {min} and {max} characters")
    private String title;

    @Nullable
    @Size(min = 3, max = 500, message = "Section description must be between {min} and {max} characters")
    private String description;

    @Valid
    @Nullable
    private List<ChapterRequest> chapters;

    // @Valid
    // private boolean isValid() {
    //     if (id == null && tempId == null) {
    //         return false;
    //     }
    //     if(tempId != null && (title == null || description == null)){
    //         return false;
    //     }
    //     return true;
    // }
}
