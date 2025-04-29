package emil.find_course.domains.dto.detailsProt;

import java.util.List;

import emil.find_course.domains.dto.course.SectionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionProtectedDto extends SectionDto {

    List<ChapterProtectedDto> chapters;

}
