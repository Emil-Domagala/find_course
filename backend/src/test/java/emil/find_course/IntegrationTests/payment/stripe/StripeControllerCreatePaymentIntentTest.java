package emil.find_course.IntegrationTests.payment.stripe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.cart.PrepareCartUtil;
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.payment.stripe.dto.PaymentIntentResponse;
import emil.find_course.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class StripeControllerCreatePaymentIntentTest extends IntegrationTestBase {
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
    private PrepareCartUtil prepareCartUtil;

    @Autowired
    private PrepareCourseUtil prepareCourseUtil;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CartRepository cartRepository;

    User user;
    String token;

    @BeforeEach
    private void setUp() {
        user = prepareUserUtil.prepareVerifiedUser();
        token = jwtUtils.generateToken(user);
    }

    private ResultActions makeRequest() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/stripe/create-payment-intent")
                .cookie(new Cookie(authCookieName, token)));
    }

    private PaymentIntentResponse extractResponse(MvcResult res) throws Exception {
        return objectMapper.readValue(res.getResponse().getContentAsString(), PaymentIntentResponse.class);
    }

    // successfully returns
    @Test
    public void StripeController_createPaymentIntent_success() throws Exception {
        var c1 = prepareCourseUtil.prepareCourse(1000);
        prepareCartUtil.prepareCart(user, Set.of(c1));
        MvcResult res = makeRequest().andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var result = extractResponse(res);
        assertThat(result.getClientSecret()).isNotNull();
        assertThat(result.getWarnings()).isNull();
    }

    // Course become draft or deleted
    @Test
    public void StripeController_createPaymentIntent_deletesInvalidCourses() throws Exception {
        var c1 = prepareCourseUtil.prepareCourse(1000);
        var c2 = prepareCourseUtil.prepareCourse(2000);
        var c3 = prepareCourseUtil.prepareCourse(3000);
        var cart = prepareCartUtil.prepareCart(user, Set.of(c1, c2, c3));
        entityManager.flush();
        entityManager.clear();

        c1.setStatus(emil.find_course.course.enums.CourseStatus.DRAFT);
        courseRepository.delete(c2);
        courseRepository.save(c1);
        entityManager.flush();
        entityManager.clear();

        MvcResult res = makeRequest().andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        var result = extractResponse(res);
        assertThat(result.getClientSecret()).isNotNull();
        assertThat(result.getWarnings()).isNotEmpty();
        assertThat(cartRepository.findById(cart.getId()).orElseThrow().getCartItems().size()).isEqualTo(1);

    }

    // 404 if no cart
    @Test
    public void StripeController_createPaymentIntent_returns404IfNoCart() throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser("test@testt.com", "test");
        var token = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/stripe/create-payment-intent")
                .cookie(new Cookie(authCookieName, token))).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

    }

}
