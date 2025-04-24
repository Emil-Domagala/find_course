package emil.find_course.domains.dto.detailsPub;

import java.util.List;

import emil.find_course.domains.dto.ChapterDto;
import emil.find_course.domains.dto.SectionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionPublicDto extends SectionDto {
    private List<ChapterDto> chapters;
}
