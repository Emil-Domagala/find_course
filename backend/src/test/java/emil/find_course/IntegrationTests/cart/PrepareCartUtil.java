package emil.find_course.IntegrationTests.cart;

import java.util.Set;

import org.springframework.stereotype.Component;

import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PrepareCartUtil {

    private final CartRepository cartRepository;

    public Cart prepareCart(User user, Set<Course> courses) {
        Cart cart = CartFactory.createCart(user, courses);
        return cartRepository.save(cart);
    }

}
