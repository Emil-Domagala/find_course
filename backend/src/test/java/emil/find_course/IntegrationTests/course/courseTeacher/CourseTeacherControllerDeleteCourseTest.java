package emil.find_course.IntegrationTests.course.courseTeacher;

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
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
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
public class CourseTeacherControllerDeleteCourseTest extends IntegrationTestBase {

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
        private PrepareTeacherUtil prepareTeacherUtil;
        @Autowired
        private PrepareUserUtil prepareUserUtil;
        @Autowired
        private ChapterRepository chapterRepository;
        @Autowired
        private SectionRepository sectionRepository;
        @Autowired
        private EntityManager entityManager;
        @Autowired
        private PrepareCourseUtil prepareCourseUtil;
        @Autowired
        private PrepareCourseWithStudentUtil prepareCourseWithStudentUtil;

        User student;
        User teacher;
        String authToken;
        Course course;

        @BeforeEach
        private void setup() {
                student = prepareUserUtil.prepareVerifiedUser();
                course = prepareCourseWithStudentUtil.prepareCourseWithChapters(student, 2);
                teacher = course.getTeacher();
                authToken = jwtUtils.generateToken(teacher);
        }

        @DisplayName("Should not delete course with students")
        @Test
        public void shouldNotDeleteCourseWithStudents() throws Exception {
                UUID courseId = course.getId();

                courseRepository.flush();
                userRepository.flush();
                entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders
                                .delete("/api/v1/teacher/courses/{courseId}", courseId)
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        @DisplayName("Should delete course and cleanup all references")
        @Test
        public void shouldDeleteCourseAndCleanupReferences() throws Exception {
                courseRepository.delete(course);
                assertThat(courseRepository.count()).isZero();

                var courseToDelete = prepareCourseUtil.prepareCourse();
                UUID courseId = courseToDelete.getId();
                User teacher = courseToDelete.getTeacher();
                String authToken = jwtUtils.generateToken(teacher);

                courseRepository.flush();
                userRepository.flush();
                entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders
                                .delete("/api/v1/teacher/courses/{courseId}", courseId)
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                assertThat(courseRepository.findById(courseId)).isEmpty();

                User freshTeacher = userRepository.findById(teacher.getId()).orElseThrow();
                assertThat(freshTeacher.getTeachingCourses()).isEmpty();

                User freshStudent = userRepository.findById(student.getId()).orElseThrow();
                assertThat(freshStudent.getEnrollmentCourses()).isEmpty();

                assertThat(sectionRepository.count()).isZero();
                assertThat(chapterRepository.count()).isZero();
        }

        @Test
        @DisplayName("Should return 400 if not valid UUID passed")
        public void courseTeacherController_deleteCourse_shouldReturn400IfInvalidUUIDPassed() throws Exception {

                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/v1/teacher/courses/{courseId}", "InvalidUUID")
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        }

        // 404 course not found
        @Test
        @DisplayName("Should return 404 if course not found")
        public void courseTeacherController_deleteCourse_shouldReturn404IfCourseNotFound() throws Exception {

                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/v1/teacher/courses/{courseId}", UUID.randomUUID())
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
        }

        // 400 dont own course
        @Test
        @DisplayName("Should return 400 if user is not owner of deleating course")
        public void courseTeacherController_deleteCourse_shouldReturn400IfUserIsNotOwnerOfDeleatingCourse()
                        throws Exception {
                var teacher2 = prepareTeacherUtil.prepareUniqueTeacher();
                var authToken2 = jwtUtils.generateToken(teacher2);
                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/v1/teacher/courses/{courseId}", course.getId())
                                                .cookie(new Cookie(authCookieName, authToken2)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        }

        // 403 no teacher
        @Test
        @DisplayName("Should return 403 if user is not teacher")
        public void courseTeacherController_deleteCourse_shouldReturn403IfUserIsNotTeacher() throws Exception {
                var authToken2 = jwtUtils.generateToken(student);
                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/v1/teacher/courses/{courseId}", course.getId())
                                                .cookie(new Cookie(authCookieName, authToken2)))
                                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();

        }

        @Test
        @DisplayName("Should return 499 if cookie not added")
        public void courseTeacherController_deleteCourse_shouldReturn499IfCookieNotAdded() throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/v1/teacher/courses/{courseId}", course.getId()))
                                .andExpect(MockMvcResultMatchers.status().is(499)).andReturn();
        }

}
