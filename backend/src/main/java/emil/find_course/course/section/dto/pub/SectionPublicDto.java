package emil.find_course.course.section.dto.pub;

import java.util.List;

import emil.find_course.course.chapter.dto.ChapterDto;
import emil.find_course.course.section.dto.SectionDto;
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
public class SectionPublicDto extends SectionDto {
    private List<ChapterDto> chapters;
}
