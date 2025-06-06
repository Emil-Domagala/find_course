package emil.find_course.teacherApplication.dto;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
}
