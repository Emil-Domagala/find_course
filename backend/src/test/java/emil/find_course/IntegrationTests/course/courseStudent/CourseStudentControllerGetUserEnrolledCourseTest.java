package emil.find_course.IntegrationTests.course.courseStudent;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.CourseFactory;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.dto.CourseDtoWithFirstChapter;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseStudentControllerGetUserEnrolledCourseTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PrepareCourseWithStudentUtil prepareCourseWithStudentUtil;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PrepareTeacherUtil prepareTeacherUtil;
    @Autowired
    private PrepareUserUtil prepareUserUtil;

    User user;
    String authToken;

    @BeforeEach
    public void setup() {
        user = prepareUserUtil.prepareVerifiedUser();
        authToken = jwtUtils.generateToken(user);
    }

    private PagingResult<CourseDtoWithFirstChapter> extractContent(MvcResult res) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PagingResult.class,
                CourseDtoWithFirstChapter.class);

        return objectMapper.readValue(res.getResponse().getContentAsString(), type);
    }

    // sucessfully returns one course
    @Test
    @DisplayName("Should sucessfully returns one course")
    public void courseStudentController_getUserEnrolledCourses_shouldSucessfullyReturnsOneCourse() throws Exception {
        var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);

        MvcResult res = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/student/courses").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        PagingResult<CourseDtoWithFirstChapter> content = extractContent(res);
        var courseDto = content.getContent().stream().toList().get(0);
        assertThat(content.getContent().size()).isEqualTo(1);
        assertThat(content.getTotalElements()).isEqualTo(1);
        assertThat(courseDto.getFirstChapter()).isEqualTo(course.getSections().get(0).getChapters().get(0).getId());
    }

    // sucessfully returns multiple courses
    @Test
    @DisplayName("Should sucessfully returns multiple courses")
    public void courseStudentController_getUserEnrolledCourses_shouldSucessfullyReturnsMultipleCourse()
            throws Exception {
        int numberOfCourses = 4;

        List<UUID> firstChapterIds = new ArrayList<>();
        for (int i = 0; i < numberOfCourses; i++) {
            var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
            firstChapterIds.add(course.getSections().get(0).getChapters().get(0).getId());
        }

        MvcResult res = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/student/courses").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        PagingResult<CourseDtoWithFirstChapter> content = extractContent(res);
        var courseDtos = content.getContent().stream().toList();
        assertThat(content.getContent().size()).isEqualTo(numberOfCourses);
        assertThat(content.getTotalElements()).isEqualTo(numberOfCourses);

        for (int i = 0; i < courseDtos.size(); i++) {
            UUID firstChapterId = courseDtos.get(i).getFirstChapter();
            UUID expectedFirstChapterId = firstChapterIds.get(i);
            assertThat(firstChapterId).isEqualTo(expectedFirstChapterId);
        }
    }

    // no courses found

    @Test
    @DisplayName("Should return empty content if no enrolled course found")
    public void courseStudentController_getUserEnrolledCourses_shouldReturnEmptyContentIfNoCourseFound()
            throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var course = CourseFactory.createPublishedCourse(teacher);
        courseRepository.save(course);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/courses")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(0))
                .andReturn();
    }

    // returns sucessfully course without chapter
    @Test
    @DisplayName("Should return sucessfully course without chapter")
    public void courseStudentController_getUserEnrolledCourses_shouldReturnSucessfullyCourseWithoutChapter()
            throws Exception {
        var course = CourseFactory.createPublishedCourse(prepareTeacherUtil.prepareUniqueTeacher());
        course.getStudents().add(user);
        user.getEnrollmentCourses().add(course);
        courseRepository.save(course);
        userRepository.save(user);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/courses")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andReturn();

        CourseDtoWithFirstChapter content = extractContent(res).getContent().stream().toList().get(0);
        assertThat(content.getFirstChapter()).isNull();

    }

    // Pagination works
    @ParameterizedTest(name = "Pagination works=> size {0}, total elements {1}, current page {2}")
    @CsvSource({
            "5,10,0",
            "10,10,0",
            "5,10,1",
    })
    public void courseStudentController_getUserEnrolledCourses_paginationWorks(String size, String totEle,
            String currPage) throws Exception {
        for (int i = 0; i < Integer.parseInt(totEle); i++) {
            prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/courses")
                .cookie(new Cookie(authCookieName, authToken))
                .param("page", currPage)
                .param("size", size))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(totEle))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page")
                        .value(Integer.parseInt(currPage) + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(size))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))
                .andReturn();

    }

    // Invalid size,page,sortField then to default
    @Test
    @DisplayName("Should return to default page, size, sortField if passed incorect val")
    public void courseStudentController_getUserEnrolledCourses_shouldReturnToDefaultPageSizeSortField()
            throws Exception {
        int numberOfCourses = 4;

        for (int i = 0; i < numberOfCourses; i++) {
            prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        }
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/courses")
                .cookie(new Cookie(authCookieName, authToken))
                .param("page", "-1")
                .param("size", "-5")
                .param("sortField", "unknownField"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(
                        PaginationRequest.DEFAULT_PAGE + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(
                        PaginationRequest.DEFAULT_SIZE))
                .andReturn();

        List<CourseDtoWithFirstChapter> courses = extractContent(res).getContent().stream().toList();

        // is ASC and createdAt
        for (int i = 0; i < numberOfCourses - 1; i++) {
            LocalDateTime current = courses.get(i).getCreatedAt();
            LocalDateTime next = courses.get(i + 1).getCreatedAt();
            assertThat(current.isBefore(next)).isTrue();
        }

    }

    @Test
    @DisplayName("Should return to default page, size, sortField if not passed")
    public void courseStudentController_getUserEnrolledCourses_shouldReturnToDefaultPageSizeSortFieldIfNotPassed()
            throws Exception {
        int numberOfCourses = 4;

        for (int i = 0; i < numberOfCourses; i++) {
            prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        }
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/courses")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(
                        PaginationRequest.DEFAULT_PAGE + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(
                        PaginationRequest.DEFAULT_SIZE))
                .andReturn();

        List<CourseDtoWithFirstChapter> courses = extractContent(res).getContent().stream().toList();

        // is ASC and createdAt
        for (int i = 0; i < numberOfCourses - 1; i++) {
            LocalDateTime current = courses.get(i).getCreatedAt();
            LocalDateTime next = courses.get(i + 1).getCreatedAt();
            assertThat(current.isBefore(next)).isTrue();
        }

    }

    // Valid sortField works //////////////////////
    @ParameterizedTest(name = "Valid sortField works=> {0}")
    @CsvSource({ "createdAt", "title", "updatedAt", "id" })
    public void courseStudentController_getUserEnrolledCourses_validSortFieldWorks(String sortField) throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var course1 = CourseFactory.createPublishedCourse(teacher);
        course1.setTitle("bbb");
        course1.getStudents().add(user);
        courseRepository.save(course1);
        var course2 = CourseFactory.createPublishedCourse(teacher);
        course2.setTitle("aaa");
        course2.getStudents().add(user);
        courseRepository.save(course2);
        var course3 = CourseFactory.createPublishedCourse(teacher);
        course3.setTitle("ccc");
        course3.getStudents().add(user);
        courseRepository.save(course3);
        user.getEnrollmentCourses().add(course1);
        user.getEnrollmentCourses().add(course2);
        user.getEnrollmentCourses().add(course3);
        userRepository.save(user);

        MvcResult res = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/student/courses").cookie(new Cookie(authCookieName, authToken))
                        .param("sortField", sortField))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        PagingResult<CourseDtoWithFirstChapter> content = extractContent(res);
        var courseDtos = content.getContent().stream().toList();
        assertThat(content.getContent().size()).isEqualTo(3);
        for (int i = 0; i < courseDtos.size() - 1; i++) {
            CourseDtoWithFirstChapter curr = courseDtos.get(i);
            CourseDtoWithFirstChapter next = courseDtos.get(i + 1);
            if (sortField.equals("createdAt")) {
                assertThat(curr.getCreatedAt().isBefore(next.getCreatedAt())).isTrue();
            } else if (sortField.equals("title")) {
                assertThat(curr.getTitle().compareTo(next.getTitle()) < 0).isTrue();

            } else if (sortField.equals("updatedAt")) {
                assertThat(curr.getUpdatedAt().isBefore(next.getUpdatedAt())).isTrue();

            } else if (sortField.equals("id")) {
                assertThat(curr.getId().toString().compareTo(next.getId().toString()) < 0).isTrue();
            }
        }

    }

    // direction works
    @ParameterizedTest(name = "direction works=> {0}")
    @CsvSource({ "ASC", "DESC" })
    public void courseStudentController_getUserEnrolledCourses_directionWorks(String direction) throws Exception {
        int numberOfCourses = 4;

        for (int i = 0; i < numberOfCourses; i++) {
            prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        }

        MvcResult res = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/student/courses").cookie(new Cookie(authCookieName, authToken))
                        .param("direction", direction)
                        .param("sortField", "createdAt"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        PagingResult<CourseDtoWithFirstChapter> content = extractContent(res);
        var courseDtos = content.getContent().stream().toList();
        for (int i = 0; i < courseDtos.size() - 1; i++) {
            CourseDtoWithFirstChapter curr = courseDtos.get(i);
            CourseDtoWithFirstChapter next = courseDtos.get(i + 1);

            if ("ASC".equals(direction)) {
                assertThat(curr.getCreatedAt().isBefore(next.getCreatedAt())).isTrue();
            } else {
                assertThat(curr.getCreatedAt().isAfter(next.getCreatedAt())).isTrue();
            }
        }
    }

    @Test
    @DisplayName("Should return 401 if user not logged in")
    public void courseStudentController_getUserEnrolledCourses_shouldReturn401IfUserNotLoggedIn() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/student/courses"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
    }

    // ERROR: direction
    @Test
    @DisplayName("Should return 400 if invalid direction")
    public void courseStudentController_getUserEnrolledCourses_shouldReturn400IfInvalidDirection() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/student/courses").cookie(new Cookie(authCookieName, authToken))
                        .param("direction", "invalidDirection"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

}
