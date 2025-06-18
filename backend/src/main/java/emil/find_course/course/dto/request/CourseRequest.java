package emil.find_course.course.dto.request;

import java.util.List;
import java.util.UUID;

import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.enums.Level;
import emil.find_course.course.section.dto.request.SectionRequest;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {

    private UUID id;

    @Nullable
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
    private String title;

    @Nullable
    @Size(min = 3, max = 1000, message = "Description must be between {min} and {max} characters")
    private String description;

    @Nullable
    private CourseCategory category;

    @Nullable
    @Positive(message = "Price must be a positive value")
    private Integer price;

    @Nullable
    // @ValidEnum(enumClass = Level.class, message = "Invalid level value")
    private Level level;

    @Nullable
    // @ValidEnum(enumClass = CourseStatus.class, message = "Invalid course status
    // value")
    private CourseStatus status;

    @Nullable
    @Valid
    private List<SectionRequest> sections;

}
