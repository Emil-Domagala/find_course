package emil.find_course.cart;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    @Transactional
    public Cart removeCourseFromCart(User user, Course course) {
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (!cart.getCourses().contains(course)) {
            return null;
        }
        if (cart.getCourses().size() == 1) {
            cartRepository.delete(cart);
            return null;
        }
        cart.setTotalPrice(cart.getTotalPrice() - course.getPrice());
        cart.getCourses().remove(course);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart addCourseToCart(User user, Course course) {
        Cart cart = cartRepository.findByUser(user).orElse(new Cart());

        if (course.getTeacher().equals(user)) {
            throw new IllegalArgumentException("You cannot add your own course to cart");
        }

        // TODO: Optimize this. Do not load whole enrolled courses write smth like
        // boolean isUserEnrolled(@Param("userId") UUID userId, @Param("courseId") UUID
        // courseId);
        if (cart.getCourses().contains(course)) {
            throw new IllegalArgumentException("You already have this course in your cart");
        }

        if (user.getEnrollmentCourses().contains(course)) {
            throw new IllegalArgumentException("You already have this course in your enrollment");
        }

        if (cart.getUser() == null) {
            cart.setUser(user);
            cart.setExpiration(Instant.now().plusSeconds(60 * 60 * 24 * 7));
        }

        cart.getCourses().add(course);

        cart.setTotalPrice(cart.getTotalPrice() + course.getPrice());

        return cartRepository.save(cart);

    }

    @Override
    public Optional<Cart> getCart(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    @Override
    public void deleteCart(Cart cart) {
        cartRepository.delete(cart);
    }

}
