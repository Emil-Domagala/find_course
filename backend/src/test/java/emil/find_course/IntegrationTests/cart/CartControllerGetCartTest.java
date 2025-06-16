package emil.find_course.IntegrationTests.cart;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;

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
}
