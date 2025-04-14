package emil.find_course.domains.dto;

import java.util.HashSet;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    @Builder.Default
    private HashSet<CourseDto> courses = new HashSet<>();
    private double totalPrice;

}
