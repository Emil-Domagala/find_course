package emil.find_course.cart;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.cart.dto.CartDto;
import emil.find_course.cart.dto.response.CartResponse;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.cart.mapper.CartMapper;
import emil.find_course.cart.repository.CartItemRepository;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.course.coursePublic.CoursePublicService;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cart Controller", description = "Endpoints for cart")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final CoursePublicService coursePublicService;
    private final CartItemRepository cartItemRepository;

    @Operation(summary = "Add course to cart")
    @PostMapping("/cart/{courseId}")
    public ResponseEntity<CartDto> addCourseToCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        final User user = userDetails.getUser();
        Course course = coursePublicService.getPublishedCourse(courseId);
        CartDto cart = cartMapper.toDto(cartService.addCourseToCart(user, course));
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Remove course from cart")
    @DeleteMapping("/cart/{courseId}")
    public ResponseEntity<CartResponse> removeCourseFromCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        final User user = userDetails.getUser();
        Optional<CartItem> cartItem = cartItemRepository.findByUserAndCourseId(user.getId(), courseId);

        var res = cartService.removeCourseFromCart(user, cartItem);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "Get user cart")
    @GetMapping("/cart")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        final User user = userDetails.getUser();
        var res = cartService.getValidCart(user);

        return ResponseEntity.ok(res);

    }

}
