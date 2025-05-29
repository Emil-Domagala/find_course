package emil.find_course.domains.requestDto.course;

import java.util.List;
import java.util.UUID;

import emil.find_course.common.validation.ValidEnum;
import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.enums.CourseStatus;
import emil.find_course.domains.enums.Level;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseRequest {

    private UUID id;

    @Nullable
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
    private String title;

    @Nullable
    @Size(min = 3, max = 1000, message = "Description must be between {min} and {max} characters")
    private String description;

    @Nullable
    @ValidEnum(enumClass = CourseCategory.class, message = "Invalid course category value")
    private CourseCategory category;

    @Nullable
    @Positive(message = "Price must be a positive value")
    private Integer price;

    @Nullable
    @ValidEnum(enumClass = Level.class, message = "Invalid level value")
    private Level level;

    @Nullable
    @ValidEnum(enumClass = CourseStatus.class, message = "Invalid course status value")
    private CourseStatus status;

    @Nullable
    private List<SectionRequest> sections;

}
