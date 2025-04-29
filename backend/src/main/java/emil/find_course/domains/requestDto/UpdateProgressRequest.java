package emil.find_course.domains.requestDto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProgressRequest {

    @NotNull(message = "Chapter Progress ID is required")
    private UUID chapterProgressId;

    private boolean completed;

}
