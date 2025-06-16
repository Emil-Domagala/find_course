package emil.find_course.cart;

import java.util.Optional;

import emil.find_course.cart.dto.response.CartResponse;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;

public interface CartService {

    public CartResponse removeCourseFromCart(User user, Optional<CartItem> cartItem);

    public Cart addCourseToCart(User user, Course course);

    public CartResponse getValidCart(User user);

    public Cart findByUserWithItemsAndCourses(User user);

    public void deleteCart(Cart cart);

}