package emil.find_course.courseProgress.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProgressRequest {

    @NotNull(message = "Chapter Progress ID is required")
    private UUID chapterProgressId;

    private boolean completed;

}
