package emil.find_course.domains.dto.detailsPub;

import java.util.List;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.entities.course.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsPublicDto {
    private CourseDto courseDto;
    private List<Section> sections;
}
