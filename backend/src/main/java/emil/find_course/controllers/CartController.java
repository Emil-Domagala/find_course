package emil.find_course.controllers;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.CartDto;
import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.mapping.CartMapping;
import emil.find_course.services.CartService;
import emil.find_course.services.CourseService;
import emil.find_course.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CartController {

    private final UserService userService;
    private final CartService cartService;
    private final CartMapping cartMapping;
    private final CourseService courseService;

    @PostMapping("/cart/{courseId}")
    public ResponseEntity<CartDto> addCourseToCart(Principal principal, @PathVariable UUID courseId) {
        User user = userService.findByEmail(principal.getName());
        Course course = courseService.getPublishedCourse(courseId);
        CartDto cart = cartMapping.toDto(cartService.addCourseToCart(user, course));
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/cart/{courseId}")
    public ResponseEntity<CartDto> removeCourseFromCart(Principal principal, @PathVariable UUID courseId) {
        User user = userService.findByEmail(principal.getName());
        Course course = courseService.getPublishedCourse(courseId);
        Cart cart = cartService.removeCourseFromCart(user, course);
        if (cart == null) {
            return ResponseEntity.ok(new CartDto());
        }
        return ResponseEntity.ok(cartMapping.toDto(cart));
    }

    @GetMapping("/cart")
    public ResponseEntity<CartDto> getCart(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Optional<Cart> cartOpt = cartService.getCart(user);

        CartDto cartDto = cartOpt.map(cartMapping::toDto).orElse(new CartDto());

        return ResponseEntity.ok(cartDto);

    }

}
