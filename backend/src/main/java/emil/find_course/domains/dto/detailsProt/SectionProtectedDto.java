package emil.find_course.domains.dto.detailsProt;

import java.util.List;

import emil.find_course.domains.dto.SectionDto;
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
public class SectionProtectedDto {

    SectionDto sectionDto;
    List<ChapterProtectedDto> chapters;

}
