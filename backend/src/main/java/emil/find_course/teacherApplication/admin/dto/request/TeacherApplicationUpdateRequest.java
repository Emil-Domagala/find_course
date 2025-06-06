package emil.find_course.teacherApplication.admin.dto.request;

import java.util.UUID;

import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TeacherApplicationUpdateRequest {

    @NotBlank(message = "Id is required")
    private UUID id;

    @Nullable
    private TeacherApplicationStatus status;

    @Nullable
    private boolean seenByAdmin;

}
