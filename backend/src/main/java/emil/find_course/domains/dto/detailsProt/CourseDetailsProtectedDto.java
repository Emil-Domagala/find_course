package emil.find_course.domains.dto.detailsProt;

import java.util.List;

import emil.find_course.domains.dto.course.CourseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsProtectedDto extends CourseDto {

    private List<SectionProtectedDto> sections;
}
