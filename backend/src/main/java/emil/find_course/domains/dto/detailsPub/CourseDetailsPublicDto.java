package emil.find_course.domains.dto.detailsPub;

import java.util.List;

import emil.find_course.domains.dto.CourseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsPublicDto extends CourseDto {

    private List<SectionPublicDto> sections;
}
