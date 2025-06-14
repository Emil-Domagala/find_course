package emil.find_course.IntegrationTests.course.courseTeacher;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.course.SectionFactory;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.dto.prot.CourseDetailsProtectedDto;
import emil.find_course.course.repository.CourseRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseTeacherControllerGetTeacherCourseTest extends IntegrationTestBase {

        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Autowired
        private JwtUtils jwtUtils;
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private PrepareCourseUtil prepareCourseUtil;
        @Autowired
        private CourseRepository courseRepository;
        @Autowired
        private PrepareTeacherUtil prepareTeacherUtil;
        @Autowired
        private PrepareUserUtil prepareUserUtil;

        // sucessfully returns whole course
        @Test
        @DisplayName("Should sucessfully returns course")
        public void courseTeacherController_getTeacherCourse_shouldSucessfullyReturnsCourse()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(3);
                var authToken = jwtUtils.generateToken(course.getTeacher());

                MvcResult res = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/api/v1/teacher/courses/{courseId}", course.getId())
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                var courseDto = objectMapper.readValue(res.getResponse().getContentAsString(),
                                CourseDetailsProtectedDto.class);
                assertThat(courseDto.getId()).isEqualTo(course.getId());
                assertThat(courseDto.getSections().size()).isEqualTo(3);
                assertThat(courseDto.getSections().get(0).getChapters().size()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should sucessfully returns course without sections")
        public void courseTeacherController_getTeacherCourse_shouldSucessfullyReturnsCourseWithoutSections()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                var authToken = jwtUtils.generateToken(course.getTeacher());

                MvcResult res = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/api/v1/teacher/courses/{courseId}", course.getId())
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                var courseDto = objectMapper.readValue(res.getResponse().getContentAsString(),
                                CourseDetailsProtectedDto.class);
                assertThat(courseDto.getId()).isEqualTo(course.getId());
                assertThat(courseDto.getSections().size()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should sucessfully returns course without chapters")
        public void courseTeacherController_getTeacherCourse_shouldSucessfullyReturnsCourseWithoutChapters()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                var section = SectionFactory.createSection(course, 0);
                course.getSections().add(section);
                courseRepository.save(course);
                var authToken = jwtUtils.generateToken(course.getTeacher());

                MvcResult res = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/api/v1/teacher/courses/{courseId}", course.getId())
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                var courseDto = objectMapper.readValue(res.getResponse().getContentAsString(),
                                CourseDetailsProtectedDto.class);
                assertThat(courseDto.getId()).isEqualTo(course.getId());
                assertThat(courseDto.getSections().size()).isEqualTo(1);
        }

        // cant acces someone course
        @Test
        @DisplayName("Should return 400 if trying to acces someone else course")
        public void courseTeacherController_getTeacherCourse_shouldReturn400IfTryingToAccessSomeoneElseCourse()
                        throws Exception {
                var teacher = prepareTeacherUtil.prepareUniqueTeacher();
                var authToken = jwtUtils.generateToken(teacher);
                var course = prepareCourseUtil.prepareCourse();

                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses/{courseId}", course.getId())
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();

        }

        // no course then 404 returned

        @Test
        @DisplayName("Should return 404 if no course found")
        public void courseTeacherController_getTeacherCourse_shouldReturn404IfNoCourseFound()
                        throws Exception {
                var teacher = prepareTeacherUtil.prepareUniqueTeacher();
                var authToken = jwtUtils.generateToken(teacher);

                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses/{courseId}", UUID.randomUUID())
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

        }

        @Test
        @DisplayName("Should return 403 if user tries to access someone course")
        public void courseTeacherController_getTeacherCourse_shouldReturn403IfUserTriesToAccessSomeoneCourse()
                        throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var authToken = jwtUtils.generateToken(user);

                var course = prepareCourseUtil.prepareCourse();
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses/{courseId}", course.getId())
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();

        }
}
