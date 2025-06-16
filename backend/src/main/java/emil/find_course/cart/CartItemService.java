package emil.find_course.cart;

import java.util.UUID;

import emil.find_course.cart.dto.response.CartResponse;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;

public interface CartItemService {

    CartItem addCourseToCart(Course course, Cart cart);

    CartResponse filterInvalidCourses(Cart cart);

    CartItem getCartItemByUserAndCourseId(User user, UUID courseId);
}
