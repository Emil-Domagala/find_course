package emil.find_course.domains.dto.courseProgress;

import java.util.UUID;

import emil.find_course.domains.enums.ChapterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterStructure {

    private UUID id;
    private String title;
    private ChapterType type;
}
