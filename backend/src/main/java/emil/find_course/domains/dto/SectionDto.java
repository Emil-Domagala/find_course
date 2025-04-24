package emil.find_course.domains.dto;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {

    private UUID id;
    private String title;
    private String description;
    
}
