package emil.find_course.domains.dto.course;

import java.time.LocalDateTime;

import java.util.UUID;

import emil.find_course.domains.entities.user.User;
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
public class CourseWithEnrollmentCount {

    private UUID id;
    private User teacher;
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