package emil.find_course.domains.dto.course;

import java.util.UUID;

import emil.find_course.domains.enums.ChapterType;
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
