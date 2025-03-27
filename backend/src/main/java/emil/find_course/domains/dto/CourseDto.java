package emil.find_course.domains.dto;


import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;

import emil.find_course.domains.entities.course.Section;
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
    private String category;
    private String imageUrl;
    private double price;
    private Level level;
    private CourseStatus status;
    private List<Section> sections;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
