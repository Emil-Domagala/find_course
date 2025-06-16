package emil.find_course.IntegrationTests.cart;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PrepareCartUtil {

    private final CartRepository cartRepository;

    public Cart prepareCart(User user, Set<Course> courses) {
        var cart = Cart.builder().user(user).expiration(Instant.now().plusSeconds(60)).build();
        HashSet<CartItem> cartItems = new HashSet<>();
        for (Course course : courses) {
            var cartItem = CartItemFactory.createCartItem(course, cart);
            cartItems.add(cartItem);
        }
        cart.setCartItems(cartItems);
        return cartRepository.save(cart);
    }

}
