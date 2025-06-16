package emil.find_course.IntegrationTests.cart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.cart.dto.response.CartResponse;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CartControllerRemoveCourseFromCartTest extends IntegrationTestBase {
    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private PrepareCourseUtil prepareCourseUtil;

    @Autowired
    private PrepareCartUtil prepareCart;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EntityManager entityManager;

    // Happy
    // Successfully remove course from cart with multiple courses
    // - Given: A user has a cart with multiple courses including courseId
    // - When: The user deletes courseId from cart
    // - Then: Response status 200 OK, and returned cart no longer includes that
    // course
    // - calculate correct price

    private CartResponse extractResponse(MvcResult res) throws Exception {
        return objectMapper.readValue(res.getResponse().getContentAsString(), CartResponse.class);
    }

    @Test
    @DisplayName("Successfully remove course from cart with multiple courses")
    public void cartController_RemoveCourseFromCart_shouldRemoveCourseFromCart() throws Exception {
        var user = prepareUserUtil.prepareUniqueVerifiedUse();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2 = prepareCourseUtil.prepareCourse(20);
        prepareCart.prepareCart(user, Set.of(c1, c2));
        var authToken = jwtUtils.generateToken(user);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/cart/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var response = extractResponse(res);
        assertThat(response.getCartDto().getCourses().size()).isEqualTo(1);
        assertThat(response.getCartDto().getTotalPrice()).isEqualTo(20);

        entityManager.flush();
        entityManager.clear();

        Cart foundCart = cartRepository.findByUser(user).orElseThrow();
        assertThat(foundCart.getCartItems().size()).isEqualTo(1);
    }

    // Successfully remove course when it’s the only course in cart
    // - cart is deleted
    @Test
    @DisplayName("Successfully remove course from cart with one courses")
    public void cartController_RemoveCourseFromCart_shouldRemoveCourseFromCartAndDeleteIt() throws Exception {
        var user = prepareUserUtil.prepareUniqueVerifiedUse();
        var c1 = prepareCourseUtil.prepareCourse(10);
        prepareCart.prepareCart(user, Set.of(c1));
        var authToken = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/cart/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(cartRepository.findByUser(user)).isEmpty();
        assertThat(cartRepository.findByUser(user)).isEmpty();
    }

    // Exceptions

    @Test
    @DisplayName("Should not throw error if Removed course not in cart")
    public void cartController_RemoveCourseFromCart_shouldNotThrowErrorIfRemovedCourseNotInCart() throws Exception {
        var user = prepareUserUtil.prepareUniqueVerifiedUse();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2 = prepareCourseUtil.prepareCourse(20);
        prepareCart.prepareCart(user, Set.of(c1));
        var authToken = jwtUtils.generateToken(user);

        var res = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/cart/{courseId}", c2.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var response = extractResponse(res);
        assertThat(response.getCartDto().getCourses().size()).isEqualTo(1);
        assertThat(response.getCartDto().getTotalPrice()).isEqualTo(10);

        Cart foundCart = cartRepository.findByUser(user).orElseThrow();
        assertThat(foundCart.getCartItems().size()).isEqualTo(1);
    }

    // Edge case

    // User not authenticated
    // cart do not exist
    @Test
    @DisplayName("Should throw 404 if cart do not exist")
    public void cartController_RemoveCourseFromCart_shouldThrow404IfCartDoesNotExist() throws Exception {
        var user = prepareUserUtil.prepareUniqueVerifiedUse();
        var c2 = prepareCourseUtil.prepareCourse(20);
        var authToken = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/cart/{courseId}", c2.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    // course price change
    @Test
    @DisplayName("Should return this same price if course price changed")
    public void cartController_RemoveCourseFromCart_shouldNotChangePriceIfCoursePriceChanged() throws Exception {
        int expectedPrice = 10;
        int oldPrice = 20;
        int newPrice = 50;

        var user = prepareUserUtil.prepareUniqueVerifiedUse();
        var c1 = prepareCourseUtil.prepareCourse(expectedPrice);
        var c2 = prepareCourseUtil.prepareCourse(oldPrice);

        var cart = prepareCart.prepareCart(user, Set.of(c1, c2));
        var authToken = jwtUtils.generateToken(user);
        // change price
        int totalPrice = cart.getCartItems().stream().mapToInt(item -> item.getPriceAtAddition()).sum();
        assertThat(totalPrice).isEqualTo(expectedPrice + oldPrice);

        var course = courseRepository.findById(c2.getId()).orElseThrow();
        course.setPrice(newPrice);
        courseRepository.save(course);

        entityManager.flush();
        entityManager.clear();

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/cart/{courseId}", c2.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var response = extractResponse(res);
        assertThat(response.getCartDto().getCourses().size()).isEqualTo(1);
        assertThat(response.getCartDto().getTotalPrice()).isEqualTo(expectedPrice);

        assertThat(courseRepository.findById(c2.getId()).get().getPrice()).isEqualTo(newPrice);
        assertThat(cartRepository.findByUser(user).get().getCartItems().size()).isEqualTo(1);
    }

    // Should delete course Item if course deleted

    @Test
    @DisplayName("Should filter invalid course and remove requested course from cart")
    public void cartController_RemoveCourseFromCart_shouldFilterInvalidCoursesAndRemoveRequested()
            throws Exception {
        var user = prepareUserUtil.prepareUniqueVerifiedUse();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2 = prepareCourseUtil.prepareCourse(20);
        var c3 = prepareCourseUtil.prepareCourse(30);
        var cart = prepareCart.prepareCart(user, Set.of(c1, c2, c3));

        var authToken = jwtUtils.generateToken(user);
        assertThat(cartRepository.findByUser(user).get().getCartItems().size()).isEqualTo(3);
        courseRepository.deleteById(c3.getId());
        entityManager.detach(cart);
        entityManager.flush();
        entityManager.clear();
        assertThat(cartRepository.findByUser(user).get().getCartItems().size()).isEqualTo(3);
        var res = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/cart/{courseId}", c2.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var response = extractResponse(res);
        assertThat(response.getWarnings().size()).isEqualTo(1);
        assertThat(response.getCartDto().getCourses().size()).isEqualTo(1);

        assertThat(cartRepository.findByUser(user).get().getCartItems().size()).isEqualTo(1);
    }

}
