package emil.find_course.IntegrationTests.auth.resetPassword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.auth.resetPassword.dto.request.ResetPasswordEmailRequest;
import emil.find_course.auth.resetPassword.entity.ResetPasswordOTT;
import emil.find_course.auth.resetPassword.repository.ResetPasswordOTTRepository;
import emil.find_course.common.service.EmailService;
import emil.find_course.user.entity.User;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ResetPasswordControllerSendResetPasswordEmailTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private PrepareResetPasswordOTT prepareResetPasswordOTT;

    @Autowired
    private ResetPasswordOTTRepository resetPasswordOTTRepository;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {

        doNothing().when(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());

    }

    @Test
    @DisplayName("Should create and send reset email OTT")
    public void resetPasswordController_SendResetPasswordEmail_shouldCreateAndSendResetEmailOTT() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        String email = user.getEmail();
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest(email);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/forgot-password")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<ResetPasswordOTT> resetPasswordOTT = resetPasswordOTTRepository.findByUser(user);

        assertThat(resetPasswordOTT).isPresent();
        verify(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    @DisplayName("Should patch existing reset email OTT")
    public void resetPasswordController_SendResetPasswordEmail_shouldPatchExistingResetEmailOTT() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        ResetPasswordOTT resetPasswordOTT = prepareResetPasswordOTT.prepareResetPasswordOTT(user);
        String resetPasOTTToken = resetPasswordOTT.getToken();
        Instant expiration = resetPasswordOTT.getExpiration();
        String email = user.getEmail();
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest(email);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/forgot-password")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<ResetPasswordOTT> resetPasswordOTTOpt = resetPasswordOTTRepository.findByUser(user);

        assertThat(resetPasswordOTTOpt).isPresent();
        assertThat(resetPasswordOTTOpt.get().getToken()).isNotEqualTo(resetPasOTTToken);
        assertThat(resetPasswordOTTOpt.get().getExpiration()).isNotEqualTo(expiration);
        verify(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());

    }

    @Test
    @DisplayName("Should return 404 error if user does not exist")
    public void resetPasswordController_SendResetPasswordEmail_shouldReturnValidationErrorIfUserDoesNotExist()
            throws Exception {
        String email = "test@tesp.com";
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest(email);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/forgot-password")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @ParameterizedTest(name = "invalid Input: email => {0}")
    @CsvSource({
            "test",
            "test@",
            "@test.com",
            "test.com",
            "test@test.",
            ".test@test.com",
            "test@test.com.",
            "test@test..com"
    })
    @DisplayName("Should return 400 if email is invalid")
    public void resetPasswordController_SendResetPasswordEmail_shouldReturn400IfEmailIsInvalid(String email)
            throws Exception {
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest(email);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/forgot-password")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 if email is empty")
    public void resetPasswordController_SendResetPasswordEmail_shouldReturn400IfEmailIsEmpty()
            throws Exception {
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/forgot-password")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
