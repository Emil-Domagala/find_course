package emil.find_course.IntegrationTests.cart;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import emil.find_course.cart.entity.Cart;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CartFactory {

    public static Cart createCart(User user, Set<Course> courses) {
        int totalPrice = courses.stream().mapToInt(Course::getPrice).sum();
        return Cart.builder().courses(new HashSet<>(courses)).totalPrice(totalPrice).user(user)
                .expiration(Instant.now().plusSeconds(60)).build();
    }

}
