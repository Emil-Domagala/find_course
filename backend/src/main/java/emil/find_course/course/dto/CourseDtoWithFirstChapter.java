package emil.find_course.course.dto;

import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseDtoWithFirstChapter extends CourseDto {
    private UUID firstChapter;
}
