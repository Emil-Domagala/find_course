package emil.find_course.cart;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import emil.find_course.cart.dto.CartDto;
import emil.find_course.cart.dto.response.CartResponse;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.cart.mapper.CartMapper;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CourseRepository courseRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemService cartItemService;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse removeCourseFromCart(User user, CartItem cartItemForDelete) {
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        CartResponse cartResponse = cartItemService.filterNullCourses(cart);

        if (!cart.getCartItems().contains(cartItemForDelete)) {
            CartDto cartDto = cartMapper.toDto(cart);
            cartResponse.setCartDto(cartDto);
            return cartResponse;
        }
        if (cart.getCartItems().size() == 1) {
            cartRepository.delete(cart);
            cartResponse.setCartDto(new CartDto());
            return cartResponse;
        }

        cart.getCartItems().remove(cartItemForDelete);
        var savedCart = cartRepository.save(cart);
        cartResponse.setCartDto(cartMapper.toDto(savedCart));
        return cartResponse;

    }

    @Override
    @Transactional
    public Cart addCourseToCart(User user, Course course) {
        Cart cart = cartRepository.findByUser(user).orElse(new Cart());

        if (courseRepository.isUserTeacher(user, course)) {
            throw new IllegalArgumentException("You cannot add your own course to cart");
        }

        if (cartItemService.isCourseInCart(course, cart)) {
            throw new IllegalArgumentException("You already have this course in your cart");
        }

        if (userRepository.isUserEnrolledInCourse(user, course)) {
            throw new IllegalArgumentException("You already have this course in your enrollment");
        }

        if (cart.getUser() == null) {
            cart.setUser(user);
            cart.setExpiration(Instant.now().plusSeconds(60 * 60 * 24 * 7));
        }

        cartItemService.addCourseToCart(course, cart);

        return cartRepository.save(cart);
    }

    @Override
    // Get valid cart no null and no draft status
    public CartResponse getValidCart(User user) {
        Cart cart = cartRepository.findByUserWithItemsAndCourses(user).orElse(new Cart());
        var cartRes = cartItemService.filterInvalidCourses(cart);

        if (cartRes.getWarnings() != null && cartRes.getWarnings().size() > 0) {
            cartRepository.save(cart);
        }

        return cartRes;
    }

    @Override
    public Cart findByUserWithItemsAndCourses(User user) {
        return cartRepository.findByUserWithItemsAndCourses(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    @Override
    public void deleteCart(Cart cart) {
        cartRepository.delete(cart);
    }

}
