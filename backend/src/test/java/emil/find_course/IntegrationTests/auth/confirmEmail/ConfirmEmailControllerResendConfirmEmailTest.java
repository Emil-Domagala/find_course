package emil.find_course.IntegrationTests.auth.confirmEmail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.auth.confirmEmail.entity.ConfirmEmailOTT;
import emil.find_course.auth.confirmEmail.repository.ConfirmEmailOTTRepository;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.service.EmailService;
import emil.find_course.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ConfirmEmailControllerResendConfirmEmailTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private ConfirmEmailOTTRepository confirmEmailOTTRepository;
    @Autowired
    private PrepareConfirmEmailOTT prepareConfirmEmailOTT;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {

        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());

    }

    @Test
    @DisplayName("Should create and resend confirm email successfully")
    public void confirmEmailController_resendConfirmEmail_sucessfullyResendConfirmEmail() throws Exception {
        User user = prepareUserUtil.prepareNotVerifiedUser();
        String authToken = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/confirm-email/resend").cookie(new Cookie(authCookieName,
                authToken)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        assertThat(confirmEmailOTTRepository.findByUser(user).isPresent());
        verify(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    @DisplayName("Should patch existing confirm email token")
    public void confirmEmailController_resendConfirmEmail_sucessfullyPatchExistingConfirmEmailToken()
            throws Exception {
        User user = prepareUserUtil.prepareNotVerifiedUser();
        ConfirmEmailOTT confirmEmailOTT = prepareConfirmEmailOTT.prepareConfirmEmailOTT(user);
        String originalToken = confirmEmailOTT.getToken();
        Instant originalExpiration = confirmEmailOTT.getExpiration();
        String authToken = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/confirm-email/resend").cookie(new Cookie(authCookieName,
                authToken)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Optional<ConfirmEmailOTT> confirmEmailOTTOptional = confirmEmailOTTRepository.findByUser(user);

        assertThat(confirmEmailOTTOptional.isPresent());
        assertThat(confirmEmailOTTOptional.get().getToken()).isNotEqualTo(originalToken);
        assertThat(confirmEmailOTTOptional.get().getExpiration()).isNotEqualTo(originalExpiration);

        verify(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());

    }

    // User is verified error
    @Test
    @DisplayName("Should return 400 when user is verified")
    public void confirmEmailController_resendConfirmEmail_returns400WhenUserIsVerified() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();

        String authToken = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/confirm-email/resend").cookie(new Cookie(authCookieName,
                authToken)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertThat(confirmEmailOTTRepository.findByUser(user)).isEmpty();
        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());

    }

}
