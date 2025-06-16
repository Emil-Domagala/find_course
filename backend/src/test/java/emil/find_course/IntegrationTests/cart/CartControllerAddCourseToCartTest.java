package emil.find_course.IntegrationTests.cart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

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
import emil.find_course.cart.dto.CartDto;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.enums.CourseStatus;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CartControllerAddCourseToCartTest extends IntegrationTestBase {
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
    private EntityManager entityManager;

    private CartDto extractResponse(MvcResult res) throws Exception {
        return objectMapper.readValue(res.getResponse().getContentAsString(), CartDto.class);
    }

    // No cart exist creates new one
    @Test
    public void cartController_addCourseToCart_shouldCreateNewCart() throws Exception {
        var c1 = prepareCourseUtil.prepareCourse(10);

        entityManager.flush();
        entityManager.clear();

        var user = prepareUserUtil.prepareVerifiedUser();
        var authToken = jwtUtils.generateToken(user);
        var res = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/cart/{courseId}", c1.getId())
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var result = extractResponse(res);
        assertThat(result.getCourses().size()).isEqualTo(1);
        assertThat(result.getTotalPrice()).isEqualTo(10);

    }

    // adds to existing cart
    @Test
    public void cartController_addCourseToCart_shouldAddToExistingCart() throws Exception {
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2 = prepareCourseUtil.prepareCourse(20);
        var user = prepareUserUtil.prepareVerifiedUser();
        prepareCart.prepareCart(user, Set.of(c1));
        var authToken = jwtUtils.generateToken(user);
        entityManager.flush();
        entityManager.clear();

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/{courseId}", c2.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractResponse(res);

        assertThat(result.getCourses().size()).isEqualTo(2);
        assertThat(result.getTotalPrice()).isEqualTo(30);

    }

    // cant add course that you teach
    @Test
    public void cartController_addCourseToCart_shouldNotAddCourseThatYouTeach() throws Exception {
        var c1 = prepareCourseUtil.prepareCourse(10);
        var authToken = jwtUtils.generateToken(c1.getTeacher());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    // cant add course that is alredy owned by you
    @Test
    public void cartController_addCourseToCart_shouldNotAddCourseThatIsAlredyOwnedByYou() throws Exception {
        var c1 = prepareCourseUtil.prepareCourse(10);
        var user = prepareUserUtil.prepareVerifiedUser();
        c1.setStudents(Set.of(user));
        entityManager.flush();
        entityManager.clear();
        var authToken = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    }

    // cant duplicate course in cart
    @Test
    public void cartController_addCourseToCart_shouldNotDuplicateCourseInCart() throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser();
        var c1 = prepareCourseUtil.prepareCourse(10);
        prepareCart.prepareCart(user, Set.of(c1));
        var authToken = jwtUtils.generateToken(user);
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    }

    // 404 if added course isa not published
    @Test
    public void cartController_addCourseToCart_shouldThrow404IfAddedCourseIsNotPublished() throws Exception {
        var c1 = prepareCourseUtil.prepareCourse(10);
        c1.setStatus(CourseStatus.DRAFT);
        entityManager.flush();
        entityManager.clear();
        var user = prepareUserUtil.prepareVerifiedUser();
        var authToken = jwtUtils.generateToken(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

    }

    // 404 if course dont exist
    @Test
    public void cartController_addCourseToCart_shouldThrow404IfACourseNotExist() throws Exception {

        var user = prepareUserUtil.prepareVerifiedUser();
        var authToken = jwtUtils.generateToken(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cart/{courseId}", UUID.randomUUID())
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

    }

}
