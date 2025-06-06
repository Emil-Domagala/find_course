package emil.find_course.IntegrationTests.auth.confirmEmail;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.user.repository.UserRepository;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ConfirmEmailControllerConfirmEmailTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookieHelperTest cookieHelper;

    @Autowired
    private JwtUtils jwtUtils;

    // Sucess path:
    // - sets email verified
    // - delets OTT
    // - saves user
    // - sends new authToken

    // Invalid passed OTT
    // OTT expired
    // Didnt found OTT
    // User do not exist
    // Verified user tries to verifie again
    // No cookie or bad token
}
