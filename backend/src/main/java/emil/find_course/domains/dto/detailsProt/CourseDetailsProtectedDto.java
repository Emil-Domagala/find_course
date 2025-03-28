package emil.find_course.domains.dto.detailsProt;

import java.util.List;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.entities.course.Section;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsProtectedDto {
    private CourseDto courseDto;
    private List<Section> sections;
}
