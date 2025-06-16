package emil.find_course.cart.dto.response;

import java.util.List;

import emil.find_course.cart.dto.CartDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private CartDto cartDto;
    private List<String> warnings;
}
