package emil.find_course.domains.dto;

import java.util.HashSet;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private UUID id;
    @Builder.Default
    private HashSet<CourseDto> courses = new HashSet<>();
    private int totalPrice;

}
