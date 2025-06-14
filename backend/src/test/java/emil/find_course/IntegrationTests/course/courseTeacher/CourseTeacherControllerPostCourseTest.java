package emil.find_course.IntegrationTests.course.courseTeacher;

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


import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseTeacherControllerPostCourseTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PrepareTeacherUtil prepareTeacherUtil;
    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Test
    @DisplayName("Sucessfully created course")
    public void courseTeacherController_postCourse_sucessfullyCreatedCourse() throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teacher/courses")
                .cookie(new Cookie(authCookieName, authToken))).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Course course = courseRepository.findAll().stream().toList().get(0);
        assertThat(course.getTitle()).isEqualTo("Untitled Course");
        assertThat(course.getTeacher()).isEqualTo(teacher);
    }

    @Test
    @DisplayName("User cant create course")
    public void courseTeacherController_postCourse_userCantCreateCourse() throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser();
        var authToken = jwtUtils.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teacher/courses")
                .cookie(new Cookie(authCookieName, authToken))).andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }
}
