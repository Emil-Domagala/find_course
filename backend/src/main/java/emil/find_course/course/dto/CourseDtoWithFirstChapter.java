package emil.find_course.course.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CourseDtoWithFirstChapter extends CourseDto {
    private UUID firstChapter;
}
