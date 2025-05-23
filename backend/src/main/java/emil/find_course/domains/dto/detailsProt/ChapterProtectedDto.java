package emil.find_course.domains.dto.detailsProt;

import emil.find_course.domains.dto.course.ChapterDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProtectedDto extends ChapterDto {

    private String content;
    private String videoUrl;
}
