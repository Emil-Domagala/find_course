package emil.find_course.courseProgress.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressDto {
    private UUID id;
    private CourseStructure course;
    private List<SectionProgressDto> sections;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int overallProgress;

}
