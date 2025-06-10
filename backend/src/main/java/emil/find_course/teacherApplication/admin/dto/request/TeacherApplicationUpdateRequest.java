package emil.find_course.teacherApplication.admin.dto.request;

import java.util.UUID;

import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherApplicationUpdateRequest {

    @NotNull(message = "Id is required or invalid format")
    private UUID id;

    @Nullable
    private TeacherApplicationStatus status;

    @Nullable
    private Boolean seenByAdmin;

}
