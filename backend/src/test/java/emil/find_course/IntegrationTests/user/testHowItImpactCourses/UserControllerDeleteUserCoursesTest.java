package emil.find_course.IntegrationTests.user.testHowItImpactCourses;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.chapter.repository.ChapterRepository;
import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.course.section.repository.SectionRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerDeleteUserCoursesTest extends IntegrationTestBase {
    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PrepareUserUtil prepareUserUtil;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PrepareCourseWithStudentUtil prepareCourseWithStudentUtil;

    User student;
    User teacher;

    Course course;

    @BeforeEach
    private void setup() {
        student = prepareUserUtil.prepareVerifiedUser();
        course = prepareCourseWithStudentUtil.prepareCourseWithChapters(student, 2);
        teacher = course.getTeacher();

    }

    @DisplayName("Should delete teacher and cleanup all references")
    @Test
    public void userController_deleteUser_shouldDeleteTeacherAndCleanupAllReferences() throws Exception {
        UUID courseId = course.getId();
        User teacher = course.getTeacher();

        courseRepository.flush();
        userRepository.flush();
        entityManager.clear();

        String authToken = jwtUtils.generateToken(teacher);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/user", courseId)
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        assertThat(courseRepository.findById(courseId)).isEmpty();

        assertThat(userRepository.findById(teacher.getId())).isEmpty();

        User freshStudent = userRepository.findById(student.getId()).orElseThrow();
        assertThat(freshStudent.getEnrollmentCourses()).isEmpty();

        assertThat(sectionRepository.count()).isZero();
        assertThat(chapterRepository.count()).isZero();
    }

    @DisplayName("Should delete user and cleanup all references")
    @Test
    public void userController_deleteUser_shouldDeleteUserAndCleanupAllReferences() throws Exception {
        UUID courseId = course.getId();
        UUID userId = student.getId();
        User teacher = course.getTeacher();

        courseRepository.flush();
        userRepository.flush();
        entityManager.clear();

        String authToken = jwtUtils.generateToken(student);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/user", courseId)
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        Course freshCourse = courseRepository.findById(courseId).orElseThrow();

        assertThat(freshCourse.getStudents().size()).isZero();

        assertThat(userRepository.findById(teacher.getId())).isNotEmpty();
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}
