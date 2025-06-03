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
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.mapper.CartMapping;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.course.CourseService;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapping cartMapping;
    private final CourseService courseService;

    @PostMapping("/cart/{courseId}")
    public ResponseEntity<CartDto> addCourseToCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        final User user = userDetails.getUser();
        Course course = courseService.getPublishedCourse(courseId);
        CartDto cart = cartMapping.toDto(cartService.addCourseToCart(user, course));
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/cart/{courseId}")
    public ResponseEntity<CartDto> removeCourseFromCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        final User user = userDetails.getUser();
        Course course = courseService.getPublishedCourse(courseId);
        Cart cart = cartService.removeCourseFromCart(user, course);
        if (cart == null) {
            return ResponseEntity.ok(new CartDto());
        }
        return ResponseEntity.ok(cartMapping.toDto(cart));
    }

    @GetMapping("/cart")
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        final User user = userDetails.getUser();
        Optional<Cart> cartOpt = cartService.getCart(user);

        CartDto cartDto = cartOpt.map(cartMapping::toDto).orElse(new CartDto());

        return ResponseEntity.ok(cartDto);

    }

}
