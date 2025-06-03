package emil.find_course.cart.dto;

import java.util.HashSet;
import java.util.UUID;

import emil.find_course.course.dto.CourseDto;
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
