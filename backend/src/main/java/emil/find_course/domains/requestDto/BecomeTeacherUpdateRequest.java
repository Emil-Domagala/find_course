package emil.find_course.domains.requestDto;

import java.util.UUID;

import emil.find_course.domains.enums.BecomeTeacherStatus;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BecomeTeacherUpdateRequest {

    @NotBlank(message = "Id is required")
    private UUID id;

    @Nullable
    private BecomeTeacherStatus status;

    @Nullable
    private boolean seenByAdmin;

}
