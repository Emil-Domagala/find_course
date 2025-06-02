package emil.find_course.course.dto;

import java.time.LocalDateTime;

import java.util.UUID;

import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.enums.Level;
import emil.find_course.user.entity.User;
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