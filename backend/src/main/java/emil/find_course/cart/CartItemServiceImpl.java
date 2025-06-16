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

@Service
@AllArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    public boolean isCourseInCart(Course course, Cart cart) {
        return cartItemRepository.isCourseInCart(course, cart);
    }

    @Override
    public CartItem addCourseToCart(Course course, Cart cart) {
        var cartItem = CartItem.builder()
                .cart(cart)
                .course(course)
                .priceAtAddition(course.getPrice())
                .build();
        cartItemRepository.save(cartItem);
        cart.getCartItems().add(cartItem);
        return cartItem;
    }

    @Override
    public CartResponse filterNullCourses(Cart cart) {
        HashSet<CartItem> validItems = new HashSet<>();
        HashSet<CartItem> invalidItems = new HashSet<>();
        var cartRes = new CartResponse();

        // clean up logic
        for (CartItem item : cart.getCartItems()) {
            if (item.getCourse() != null) {
                validItems.add(item);
            } else {
                invalidItems.add(item);
            }
        }
        if (invalidItems.size() > 0) {
            cartRes.setWarnings(
                    List.of("Some courses were removed from your cart because they are no longer available."));
        }
        cart.setCartItems(validItems);
        cartItemRepository.deleteAll(invalidItems);
        return cartRes;
    }

    @Override
    public CartResponse filterInvalidCourses(Cart cart) {
        HashSet<CartItem> validItems = new HashSet<>();
        HashSet<CartItem> invalidItems = new HashSet<>();
        var cartRes = new CartResponse();
        // clean up logic
        for (CartItem item : cart.getCartItems()) {
            if (item.getCourse() != null && item.getCourse().getStatus().equals(CourseStatus.PUBLISHED)) {
                validItems.add(item);
            } else {
                invalidItems.add(item);
            }
        }
        if (invalidItems.size() > 0) {
            cartRes.setWarnings(
                    List.of("Some courses were removed from your cart because they are no longer available."));
        }
        cart.setCartItems(validItems);
        cartItemRepository.deleteAll(invalidItems);
        return cartRes;
    }

    @Override
    public CartItem getCartItemByUserAndCourseId(User user, UUID courseId) {
        return cartItemRepository.findByUserAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
    }

}
