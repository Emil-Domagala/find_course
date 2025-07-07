package emil.find_course.IntegrationTests.teacherApplication.user;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherApplicationUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.teacherApplication.dto.TeacherApplicationDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TeacherApplicationUserControllerGetUserTeacherApplicationTest extends IntegrationTestBase {
        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private PrepareUserUtil prepareUserUtil;
        @Autowired
        private PrepareTeacherApplicationUtil prepareTeacherApplicationUtil;

        // sucessfully finds TeacherApplication and returns DTO
        @Test
        @DisplayName("Sucessfully returns TeacherApplicationDto")
        public void teacherApplicationUserController_getUserTeacherApplication_SucessfullyReturnsTeacherDto()
                        throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                prepareTeacherApplicationUtil.praparTeacherApplication(user);
                var authToken = jwtUtils.generateToken(user);

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/v1/user/teacher-application")
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                TeacherApplicationDto teacherApplicationDto = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                TeacherApplicationDto.class);

                System.out.println(teacherApplicationDto.toString());
                assertThat(teacherApplicationDto.getUser().getEmail()).isEqualTo(user.getEmail());

        }

        // if no TeacherApplication returns new TeacherApplicationDto

        @Test
        @DisplayName("Sucessfully returns empty TeacherApplicationDto")
        public void teacherApplicationUserController_getUserTeacherApplication_SucessfullyReturnsEmptyTeacherDto()
                        throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var authToken = jwtUtils.generateToken(user);

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/v1/user/teacher-application")
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                TeacherApplicationDto teacherApplicationDto = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                TeacherApplicationDto.class);

                assertThat(teacherApplicationDto.getId()).isNull();

        }

}
