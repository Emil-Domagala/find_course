package emil.find_course.IntegrationTests.teacherApplication.admin;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.teacherApplication.PrepareAdminUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherApplicationUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TeacherApplicationAdminControllerGetCountedNewTeacherApplicationsTest extends IntegrationTestBase {

        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PrepareAdminUtil prepareAdminUtil;
        @Autowired
        private PrepareUserUtil prepareUserUtil;

        @Autowired
        private PrepareTeacherApplicationUtil prepareTeacherApplicationUtil;

        @Autowired
        private ObjectMapper objectMapper;

        // Only admin can access
        @Test
        @DisplayName("Should return 403 when user is not admin")
        public void teacherApplicationAdminController_getCountedNewTeacherApplications_shouldReturn403WhenUserIsNotAdmin()
                        throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var authToken = jwtUtils.generateToken(user);

                prepareTeacherApplicationUtil.praparTeacherApplication(user);

                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application/notifications")
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());

        }

        @ParameterizedTest(name = "Should return {0} number => seenByAdmin = false {0}, true {1}")
        @CsvSource({ "0,0", "1,0", "10,0", "0,10", "10,10" })
        public void teacherApplicationAdminController_getCountedNewTeacherApplications_shouldReturnProperNumberOfNewRequests(
                        int newR, int seen) throws Exception {
                var admin = prepareAdminUtil.prepareAdmin();
                var authToken = jwtUtils.generateToken(admin);
                prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(newR);
                prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(seen, true);

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application/notifications")
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                Map<String, Integer> res = objectMapper.readValue(result.getResponse().getContentAsString(),
                                new TypeReference<Map<String, Integer>>() {
                                });

                assertThat(res.get("newRequests")).isEqualTo(newR);
        }
}
