package emil.find_course.course.chapter.dto;

import java.util.UUID;

import emil.find_course.course.chapter.enums.ChapterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDto {

    private UUID id;
    private ChapterType type;
    private String title;

}
