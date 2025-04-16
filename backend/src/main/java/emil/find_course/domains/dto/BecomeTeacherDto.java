package emil.find_course.domains.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import emil.find_course.domains.enums.BecomeTeacherStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BecomeTeacherDto {

    private UUID id;
    private UserDto user;
    private BecomeTeacherStatus status;
    private boolean seenByAdmin;
    private LocalDateTime createdAt;
}
