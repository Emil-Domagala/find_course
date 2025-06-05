package emil.find_course.course.chapter.dto.prot;

import emil.find_course.course.chapter.dto.ChapterDto;
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
public class ChapterProtectedDto extends ChapterDto {

    private String content;
    private String videoUrl;
}
