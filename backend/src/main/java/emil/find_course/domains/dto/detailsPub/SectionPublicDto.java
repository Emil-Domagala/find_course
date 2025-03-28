package emil.find_course.domains.dto.detailsPub;

import java.util.List;

import emil.find_course.domains.dto.ChapterDto;
import emil.find_course.domains.dto.SectionDto;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionPublicDto {
    private SectionDto sectionDto;
    private List<ChapterDto> chapters;
}
