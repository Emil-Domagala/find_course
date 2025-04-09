package emil.find_course.domains.requestDto;

import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.enums.CourseStatus;
import emil.find_course.domains.enums.Level;
import emil.find_course.domains.enums.Validate.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestCourseBody {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between {min} and {max} characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 500, message = "Description must be between {min} and {max} characters")
    private String description;

    @NotNull
    @ValidEnum(enumClass = CourseCategory.class, message = "Invalid course category value")
    private CourseCategory category;

    @NotBlank(message = "Image URL is required")
    @Size(min = 3, max = 255, message = "Image URL must be between {min} and {max} characters")
    @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$", message = "Image URL must be a valid URL")
    private String imageUrl;

    @Positive(message = "Price must be a positive value")
    private double price;

    @NotNull(message = "Level is required")
    @ValidEnum(enumClass = Level.class, message = "Invalid level value")
    private Level level;

    @NotNull(message = "Course Status is required")
    @ValidEnum(enumClass = CourseStatus.class, message = "Invalid course status value")
    private CourseStatus status;
}
