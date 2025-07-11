package emil.find_course.cart;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import emil.find_course.cart.dto.response.CartResponse;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.cart.repository.CartItemRepository;
import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem addCourseToCart(Course course, Cart cart) {
        var cartItem = CartItem.builder()
                .cart(cart)
                .course(course)
                .build();
        cart.getCartItems().add(cartItem);

        return cartItem;
    }

    @Override
    public CartResponse filterInvalidCourses(Cart cart) {
        HashSet<CartItem> validItems = new HashSet<>();
        HashSet<CartItem> invalidItems = new HashSet<>();
        var cartRes = new CartResponse();
        // clean up logic
        for (CartItem item : cart.getCartItems()) {
            if (item.getCourse() != null &&
                    item.getCourse().getStatus() != null &&
                    item.getCourse().getStatus().equals(CourseStatus.PUBLISHED)) {
                validItems.add(item);
            } else {
                invalidItems.add(item);
            }
        }
        if (invalidItems.size() > 0) {
            cartRes.setWarnings(
                    List.of("Some courses were removed from your cart because they are no longer available."));
        }
        log.info("invalid items size: " + invalidItems.size());
        cart.getCartItems().clear();
        cart.getCartItems().addAll(validItems);
        return cartRes;
    }

    @Override
    public CartItem getCartItemByUserAndCourseId(User user, UUID courseId) {
        return cartItemRepository.findByUserAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
    }

}
