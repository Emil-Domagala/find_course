package emil.find_course.course.dto;

import java.time.LocalDateTime;

import java.util.UUID;

import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.enums.Level;
import emil.find_course.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    private UUID id;
    private UserDto teacher;
    private String title;
    private String description;
    private CourseCategory category;
    private String imageUrl;
    private int price;
    private Level level;
    private CourseStatus status;
    private long studentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
