package emil.find_course.domains.dto;

import java.time.LocalDateTime;

import java.util.UUID;

import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.enums.CourseStatus;
import emil.find_course.domains.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    private UUID id;
    private UserDto teacher;
    private String title;
    private String description;
    private CourseCategory category;
    private String imageUrl;
    private double price;
    private Level level;
    private CourseStatus status;
    private long studentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
