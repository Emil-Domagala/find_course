package emil.find_course.course.dto.pub;

import java.util.List;

import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.section.dto.pub.SectionPublicDto;
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
public class CourseDetailsPublicDto extends CourseDto {

    private List<SectionPublicDto> sections;
}
