package emil.find_course.teacherApplication.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import emil.find_course.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherApplicationDto {

    private UUID id;
    private UserDto user;
    private TeacherApplicationStatus status;
    private boolean seenByAdmin;
    private Instant createdAt;

    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "status", "seenByAdmin", "id");

}
