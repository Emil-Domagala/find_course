package emil.find_course.course.dto.prot;

import java.util.List;

import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.section.dto.prot.SectionProtectedDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsProtectedDto extends CourseDto {

    private List<SectionProtectedDto> sections;
}
