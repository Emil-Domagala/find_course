package emil.find_course.domains.dto.courseProgress;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionProgressDto {

    private UUID id;
    private SectionStructure originalSection;
    private List<ChapterProgressDto> chapters;
}
