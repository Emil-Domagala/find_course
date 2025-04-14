package emil.find_course.services;



import java.util.Optional;

import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;

public interface CartService {

    public Cart removeCourseFromCart(User user, Course course);

    public Cart addCourseToCart(User user, Course course);

    public Optional<Cart> getCart(User user);

}
