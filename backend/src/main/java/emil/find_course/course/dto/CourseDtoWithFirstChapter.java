package emil.find_course.course.dto;

import java.util.Set;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseDtoWithFirstChapter extends CourseDto {
    private UUID firstChapter;

    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "title", "id");
}
