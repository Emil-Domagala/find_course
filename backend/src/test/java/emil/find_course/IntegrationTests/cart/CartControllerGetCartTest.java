package emil.find_course.IntegrationTests.cart;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Set;

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
import emil.find_course.cart.repository.CartItemRepository;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CartControllerGetCartTest extends IntegrationTestBase {
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
    private PrepareCartUtil prepareCart;

    @Autowired
    private PrepareCourseUtil prepareCourseUtil;

    @Autowired
    private CartItemRepository cartItemRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private EntityManager entityManager;

    private CartResponse extractResponse(MvcResult res) throws Exception {
        return objectMapper.readValue(res.getResponse().getContentAsString(), CartResponse.class);
    }

    // Returns All Valid Cart Items
    @Test
    public void cartController_getCart_shouldReturnAllValidCartItems() throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2 = prepareCourseUtil.prepareCourse(20);
        prepareCart.prepareCart(user, Set.of(c1, c2));
        var authToken = jwtUtils.generateToken(user);
        c2.setPrice(50);
        courseRepo.save(c2);

        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/cart").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var result = extractResponse(res);
        assertThat(result.getCartDto().getCourses().size()).isEqualTo(2);
        assertThat(result.getCartDto().getTotalPrice()).isEqualTo(30);

        assertThat(courseRepo.findById(c2.getId()).get().getPrice()).isEqualTo(50);

    }

    // Removes Unpublished or Deleted Courses from Cart
    @Test
    public void cartController_getCart_shouldRemoveInvalidCourses() throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2_0 = prepareCourseUtil.prepareCourse(20);
        var c2id = c2_0.getId();
        var c3_0 = prepareCourseUtil.prepareCourse(20);
        var c3id = c3_0.getId();
        prepareCart.prepareCart(user, Set.of(c1, c2_0, c3_0));
        var authToken = jwtUtils.generateToken(user);

        entityManager.flush();
        entityManager.clear();
        var c2 = courseRepo.findById(c2id).get();
        var c3 = courseRepo.findById(c3id).get();
        c3.setStatus(CourseStatus.DRAFT);
        courseRepo.delete(c2);
        courseRepo.save(c3);
        entityManager.flush();
        entityManager.clear();

        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/cart").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var result = extractResponse(res);
        assertThat(result.getCartDto().getCourses().size()).isEqualTo(1);
        assertThat(result.getCartDto().getTotalPrice()).isEqualTo(10);
        assertThat(result.getWarnings().size()).isEqualTo(1);
        assertThat(cartItemRepo.count()).isEqualTo(1);
    }

    // If cart expired new cart return and delete old one
    @Test
    public void cartController_getCart_shouldReturnNewCartAndDeleteOldOne() throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2 = prepareCourseUtil.prepareCourse(20);
        var cart = prepareCart.prepareCart(user, Set.of(c1, c2));
        var authToken = jwtUtils.generateToken(user);
        cart.setExpiration(Instant.now().minusSeconds(1));

        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/cart").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var result = extractResponse(res);

        assertThat(result.getWarnings().size() > 0).isTrue();

    }

    // returns empty cart if not exists
    @Test
    public void cartController_getCart_shouldReturnEmptyCartIfNotExist() throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser();
        var authToken = jwtUtils.generateToken(user);
        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/cart").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var result = extractResponse(res);

        assertThat(result.getCartDto()).isNull();
    }

}
