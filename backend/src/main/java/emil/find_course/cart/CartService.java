package emil.find_course.cart;

import java.util.Optional;

import emil.find_course.cart.entity.Cart;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;

public interface CartService {

    public Cart removeCourseFromCart(User user, Course course);

    public Cart addCourseToCart(User user, Course course);

    public Optional<Cart> getCart(User user);

    public Cart getCartByUser(User user);

    public void deleteCart(Cart cart);

}
