package emil.find_course.courseProgress.dto;

import java.util.UUID;

import emil.find_course.course.chapter.enums.ChapterType;
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
