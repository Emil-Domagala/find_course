package emil.find_course.courseProgress.dto;

import java.time.Instant;
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
    private Instant createdAt;
    private Instant updatedAt;
    private int overallProgress;

}
