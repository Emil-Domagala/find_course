package emil.find_course.cart;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public CartResponse removeCourseFromCart(User user, Optional<CartItem> cartItemForDelete) {
        Cart cart = cartRepository.findByUserWithItemsAndCourses(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        CartResponse cartResponse = cartItemService.filterInvalidCourses(cart);

        log.info("after filterInvalidCourses");
        if (!cartItemForDelete.isPresent()) {
            cartResponse.setCart(cartMapper.toDto(cart));
            return cartResponse;
        }
        var itemForDelete = cartItemForDelete.get();
        if (!cart.getCartItems().contains(itemForDelete)) {
            log.info("Item not found in cart");
            CartDto cartDto = cartMapper.toDto(cart);
            cartResponse.setCart(cartDto);
            return cartResponse;
        }
        if (cart.getCartItems().size() == 1) {
            log.info("Deleting cart");
            cartRepository.delete(cart);
            return cartResponse;
        }
        log.info("Deleting item from cart");
        cart.getCartItems().remove(itemForDelete);
        log.info("Saving cart");
        var savedCart = cartRepository.save(cart);
        log.info("Cart saved");
        cartResponse.setCart(cartMapper.toDto(savedCart));
        log.info("Returning cart response");
        return cartResponse;

    }

    // TODO: Optimize. Just add eager course.
    @Override
    @Transactional
    public Cart addCourseToCart(User user, Course course) {
        Cart cart = cartRepository.findByUser(user).orElse(new Cart());

        if (courseRepository.isUserTeacher(user, course)) {
            throw new IllegalArgumentException("You cannot add your own course to cart");
        }

        if (cart.getCartItems().stream().anyMatch(item -> item.getCourse().getId().equals(course.getId()))) {
            throw new IllegalArgumentException("You already have this course in your cart");
        }

        if (userRepository.isUserEnrolledInCourse(user, course)) {
            throw new IllegalArgumentException("You already have this course in your enrollment");
        }

        if (cart.getUser() == null) {
            cart.setUser(user);
            cart.setExpiration(Instant.now().plusSeconds(60 * 60 * 24));
        }

        cartItemService.addCourseToCart(course, cart);

        return cartRepository.save(cart);
    }

    @Override
    // Get valid cart no null and no draft status
    public CartResponse getValidCart(User user) {
        log.info("Getting valid cart");
        Cart cart = cartRepository.findByUserWithItemsAndCourses(user).orElse(new Cart());
        log.info("Cart found");
        if (cart.getUser() == null) {
            log.info("Cart not found");
            var cartRes = CartResponse.builder().build();
            return cartRes;
        }

        if (cart.getExpiration().isBefore(Instant.now())) {
            log.info("Cart expired");
            cartRepository.delete(cart);
            var cartRes = CartResponse.builder().warnings(List.of("Your cart has expired")).build();
            return cartRes;
        }
        var cartRes = cartItemService.filterInvalidCourses(cart);

        if (cartRes.getWarnings() != null && cartRes.getWarnings().size() > 0) {
            log.info("Cart has invalid courses");
            cartRepository.save(cart);
        }
        cartRes.setCart(cartMapper.toDto(cart));

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
