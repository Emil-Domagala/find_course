package emil.find_course.domains.dto.course;

import java.util.UUID;

import lombok.Data;

@Data
public class CourseDtoWithFirstChapter extends CourseDto {
    private UUID firstChapter;
}
