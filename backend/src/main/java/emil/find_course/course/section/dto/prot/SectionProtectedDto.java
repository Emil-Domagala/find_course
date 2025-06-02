package emil.find_course.course.section.dto.prot;

import java.util.List;

import emil.find_course.course.chapter.dto.prot.ChapterProtectedDto;
import emil.find_course.course.section.dto.SectionDto;
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
