package emil.find_course.IntegrationTests.teacherApplication.user;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherApplicationUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.teacherApplication.repository.TeacherApplicationRepository;
import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TeacherApplicationUserControllerPostTeacherApplicationTest extends IntegrationTestBase {
        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PrepareTeacherApplicationUtil prepareTeacherApplicationUtil;
        @Autowired
        private PrepareTeacherUtil prepareTeacherUtil;
        @Autowired
        private PrepareUserUtil prepareUserUtil;
        @Autowired
        private TeacherApplicationRepository teacherApplicationRepository;

        // Sucessfully creates TeacherApplication
        // - check if there is application saved
        // - status pending
        // -not seen by admin
        @Test
        @DisplayName("Sucessfully creates TeacherApplication")
        public void teacherApplicationUserController_postTeacherApplication_SucessfullyCreatesTeacherApplication()
                        throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var authToken = jwtUtils.generateToken(user);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                var savedTeacherApplication = teacherApplicationRepository.findByUser(user);
                assertThat(savedTeacherApplication).isNotEmpty();
        }

        // throws 400 when user is not verified

        @Test
        @DisplayName("Should return 403 when user is not verified")
        public void teacherApplicationUserController_postTeacherApplication_returns403WhenUserIsNotVerified()
                        throws Exception {
                var user = prepareUserUtil.prepareNotVerifiedUser();
                var authToken = jwtUtils.generateToken(user);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());

                var savedTeacherApplication = teacherApplicationRepository.findByUser(user);
                assertThat(savedTeacherApplication).isEmpty();
        }

        // throws 400 when user is already a teacher
        @Test
        @DisplayName("Should return 400 when user is already a teacher")
        public void teacherApplicationUserController_postTeacherApplication_returns400WhenUserIsAlreadyTeacher()
                        throws Exception {
                var user = prepareTeacherUtil.prepareTeacher();
                var authToken = jwtUtils.generateToken(user);
                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

                var savedTeacherApplication = teacherApplicationRepository.findByUser(user);
                assertThat(savedTeacherApplication).isEmpty();
        }

        // throws 400 when there is already teacher application
        @Test
        @DisplayName("Should return 400 when there is already teacher application of this user")
        public void teacherApplicationUserController_postTeacherApplication_returns400WhenThereIsAlreadyTeacherApplication()
                        throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var authToken = jwtUtils.generateToken(user);
                prepareTeacherApplicationUtil.praparTeacherApplication(user);
                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

                var savedTeacherApplication = teacherApplicationRepository.findByUser(user);
                assertThat(savedTeacherApplication).isNotEmpty();
        }

}
