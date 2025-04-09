package emil.find_course.domains.dto;

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
public class ChapterDto {

    private UUID id;
    private ChapterType type;
    private String title;

}
