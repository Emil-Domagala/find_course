package emil.find_course.IntegrationTests.payment.stripe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.cart.PrepareCartUtil;
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.common.service.EmailService;
import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.payment.transaction.repository.TransactionRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class StripeControllerHandleStripeWebhookTest extends IntegrationTestBase {
    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PrepareCartUtil prepareCartUtil;

    @Autowired
    private PrepareCourseUtil prepareCourseUtil;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @MockitoBean
    private EmailService emailService;

    private ResultActions createPayload(Cart cart, List<Course> courses, User user, String pIStatus) throws Exception {
        var cartId = cart.getId().toString();
        var userId = user.getId().toString();
        var coursesId = courses.stream().map(c -> c.getId().toString()).toList();
        var total = (long) cart.getCartItems().stream().mapToInt(i -> i.getPriceAtAddition()).sum();

        try (MockedStatic<Webhook> mockedStaticWebhook = mockStatic(Webhook.class)) {
            // 1. Mock PaymentIntent
            PaymentIntent mockPi = mock(PaymentIntent.class);
            when(mockPi.getId()).thenReturn("pi_test_123");
            when(mockPi.getAmount()).thenReturn(total);
            when(mockPi.getCurrency()).thenReturn("usd");
            when(mockPi.getReceiptEmail()).thenReturn(user.getEmail());

            Map<String, String> metadata = new HashMap<>();
            metadata.put("userId", userId);
            metadata.put("cartId", cartId);
            metadata.put("courseIds", String.join(",", coursesId));
            when(mockPi.getMetadata()).thenReturn(metadata);

            // 2. Mock EventDataObjectDeserializer
            EventDataObjectDeserializer mockDeserializer = mock(EventDataObjectDeserializer.class);
            when(mockDeserializer.getObject()).thenReturn(Optional.of(mockPi));

            // 3. Mock Event
            Event mockEvent = mock(Event.class);
            when(mockEvent.getId()).thenReturn("evt_test_123");
            when(mockEvent.getType()).thenReturn(pIStatus);
            when(mockEvent.getDataObjectDeserializer()).thenReturn(mockDeserializer);
            // If your service uses event.getApiVersion(), mock it too:
            // when(mockEvent.getApiVersion()).thenReturn("2020-08-27"); // Or your target
            // version

            // 4. Stub Webhook.constructEvent to return your mockEvent
            mockedStaticWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(mockEvent);

            // 5. Call your controller
            return mockMvc.perform(post("/api/v1/public/transaction/stripe/webhook")
                    .header("Stripe-Signature", "fake_signature")
                    .content("{}") // payload can be dummy
                    .contentType(MediaType.APPLICATION_JSON));
        }
    }

    @BeforeEach
    void setUp() {
        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void StripeController_handleStripeWebhook_success() throws Exception {
        var u1 = prepareUserUtil.prepareVerifiedUser();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var ca1 = prepareCartUtil.prepareCart(u1, Set.of(c1));
        var res = createPayload(ca1, List.of(c1), u1, "payment_intent.succeeded");

        res.andExpect(MockMvcResultMatchers.status().isOk());

        var fU = userRepository.findById(u1.getId()).orElseThrow();
        var fC = courseRepository.findById(c1.getId()).orElseThrow();
        assertThat(transactionRepository.count()).isEqualTo(1);
        var fT = transactionRepository.findAll().stream().findFirst().orElseThrow();

        assertThat(cartRepository.count()).isEqualTo(0);
        assertThat(fU.getEnrollmentCourses().contains(c1));
        assertThat(fC.getStudents().contains(u1));
        assertThat(fT.getAmount()).isEqualTo(10);
    }

    @Test
    void StripeController_handleStripeWebhook_successMultipleCourses() throws Exception {
        var u1 = prepareUserUtil.prepareVerifiedUser();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var c2 = prepareCourseUtil.prepareCourse(20);
        var ca1 = prepareCartUtil.prepareCart(u1, Set.of(c1, c2));
        var res = createPayload(ca1, List.of(c1, c2), u1, "payment_intent.succeeded");

        res.andExpect(MockMvcResultMatchers.status().isOk());

        var fU = userRepository.findById(u1.getId()).orElseThrow();
        var fC1 = courseRepository.findById(c1.getId()).orElseThrow();
        var fC2 = courseRepository.findById(c2.getId()).orElseThrow();
        assertThat(transactionRepository.count()).isEqualTo(1);
        var fT = transactionRepository.findAll().stream().findFirst().orElseThrow();

        assertThat(cartRepository.count()).isEqualTo(0);
        assertThat(fU.getEnrollmentCourses().containsAll(List.of(c1, c2)));
        assertThat(fC1.getStudents().contains(u1));
        assertThat(fC2.getStudents().contains(u1));
        assertThat(fT.getAmount()).isEqualTo(30);
    }

    @Test
    void StripeController_handleStripeWebhook_returnsError() throws Exception {
        var u1 = prepareUserUtil.prepareVerifiedUser();
        var c1 = prepareCourseUtil.prepareCourse(10);
        var ca1 = prepareCartUtil.prepareCart(u1, Set.of(c1));
        var res = createPayload(ca1, List.of(c1), u1, "payment_intent.payment_failed");

        res.andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

}
