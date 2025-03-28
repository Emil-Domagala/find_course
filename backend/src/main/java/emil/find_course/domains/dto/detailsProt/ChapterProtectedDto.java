package emil.find_course.domains.dto.detailsProt;

import java.util.UUID;

import emil.find_course.domains.dto.ChapterDto;
import emil.find_course.domains.entities.course.Section;
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
public class ChapterProtectedDto {
    private ChapterDto chapterDto;
    
    private String videoUrl;
    private UUID videoId;
    private String videoType;
    private Section section;
}
