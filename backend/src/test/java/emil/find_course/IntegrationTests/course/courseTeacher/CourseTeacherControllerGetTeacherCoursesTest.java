package emil.find_course.IntegrationTests.course.courseTeacher;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

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
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.repository.CourseRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseTeacherControllerGetTeacherCoursesTest extends IntegrationTestBase {

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


    private PagingResult<CourseDto> extractContent(MvcResult res) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PagingResult.class,
                CourseDto.class);

        return objectMapper.readValue(res.getResponse().getContentAsString(), type);
    }

    // Should successfully return one course
    @Test
    @DisplayName("Should successfully return one course")
    public void courseTeacherController_getTeacherCourses_shouldSuccessfullyReturnOneCourse() throws Exception {
        var course = prepareCourseUtil.prepareCourse();
        var teacher = course.getTeacher();
        var authToken = jwtUtils.generateToken(teacher);

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractContent(res);
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // Should successfully return multiple courses
    @Test
    @DisplayName("Should successfully return one course")
    public void courseTeacherController_getTeacherCourses_shouldSuccessfullyReturnMultipleCourses() throws Exception {
        var course = prepareCourseUtil.prepareCourse();
        var teacher = course.getTeacher();
        prepareCourseUtil.prepareCourse(teacher);

        var authToken = jwtUtils.generateToken(teacher);

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractContent(res);
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    // Should return draft course too:
    @Test
    @DisplayName("Should return draft course too")
    public void courseTeacherController_getTeacherCourses_shouldReturnDraftCourseToo() throws Exception {
        var course = prepareCourseUtil.prepareDraftCourse();
        var teacher = course.getTeacher();
        prepareCourseUtil.prepareCourse(teacher);
        var authToken = jwtUtils.generateToken(teacher);

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractContent(res);
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        var courses = result.getContent().stream().toList();
        var courseStatus1 = courses.get(0).getStatus();
        var courseStatus2 = courses.get(1).getStatus();
        boolean isOneDraft = courseStatus1.equals(CourseStatus.DRAFT) || courseStatus2.equals(CourseStatus.DRAFT);

        assertThat(isOneDraft).isTrue();
    }

    // Should return empty content if not owner
    @Test
    @DisplayName("Should return empty if no courses found")
    public void courseTeacherController_getTeacherCourses_shouldReturnEmptyContentIfNoCoursesFound() throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractContent(res);
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent().size()).isEqualTo(0);

    }

    // Pagination works (parameterized test)
    @ParameterizedTest
    @CsvSource({ "5,10,0", "10,10,0", "5,10,1" })
    public void courseTeacherController_getTeacherCourses_paginationWorks(String size, String totEle, String currPage)
            throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);
        for (int i = 0; i < Integer.parseInt(totEle); i++) {
            prepareCourseUtil.prepareCourse(teacher);
        }
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses").param("page", currPage)
                        .param("size", size)
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(totEle))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page")
                        .value(Integer.parseInt(currPage) + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(size))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))
                .andReturn();

    }

    // Should return to default page, size, sortField if not passed
    @Test
    @DisplayName("Should return to default page, size, sortField if not passed")
    public void courseTeacherController_getTeacherCourses_shouldReturnToDefaultIfNotPassed() throws Exception {
        int numberOfCourses = 4;
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);
        for (int i = 0; i < numberOfCourses; i++) {
            prepareCourseUtil.prepareCourse(teacher);
        }

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(
                        PaginationRequest.DEFAULT_PAGE + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(
                        PaginationRequest.DEFAULT_SIZE))
                .andReturn();

        List<CourseDto> courses = extractContent(res).getContent().stream().toList();

        // is ASC and createdAt
        for (int i = 0; i < numberOfCourses - 1; i++) {
            LocalDateTime current = courses.get(i).getCreatedAt();
            LocalDateTime next = courses.get(i + 1).getCreatedAt();
            assertThat(current.isBefore(next)).isTrue();
        }

    }

    // Should return to default page, size, sortField if passed incorrect values
    @Test
    @DisplayName("Should return to default page, size, sortField if passed invalid")
    public void courseTeacherController_getTeacherCourses_shouldReturnToDefaultIfPassedIncorectValues()
            throws Exception {
        int numberOfCourses = 4;
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);

        for (int i = 0; i < numberOfCourses; i++) {
            prepareCourseUtil.prepareCourse(teacher);
        }

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses").param("page", "-1")
                        .param("size", "-5")
                        .param("sortField", "unknownField")
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(
                        PaginationRequest.DEFAULT_PAGE + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(
                        PaginationRequest.DEFAULT_SIZE))
                .andReturn();

        List<CourseDto> courses = extractContent(res).getContent().stream().toList();

        // is ASC and createdAt
        for (int i = 0; i < numberOfCourses - 1; i++) {
            LocalDateTime current = courses.get(i).getCreatedAt();
            LocalDateTime next = courses.get(i + 1).getCreatedAt();
            assertThat(current.isBefore(next)).isTrue();
        }

    }

    // Valid sortField works (parameterized test)
    @ParameterizedTest(name = "sort field => {0}")
    @CsvSource({ "createdAt", "title", "updatedAt", "id", "price" })
    public void courseTeacherController_getTeacherCourses_validSortFieldWorks(String sortField) throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);

        var course1 = CourseFactory.createPublishedCourse(teacher);
        course1.setTitle("bbb");
        course1.setPrice(2);

        courseRepository.save(course1);
        var course2 = CourseFactory.createPublishedCourse(teacher);
        course2.setTitle("aaa");
        course2.setPrice(1);

        courseRepository.save(course2);
        var course3 = CourseFactory.createPublishedCourse(teacher);
        course3.setTitle("ccc");
        course3.setPrice(3);

        courseRepository.save(course3);

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .param("direction", "ASC")
                        .param("sortField", sortField)
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<CourseDto> courseDtos = extractContent(res).getContent().stream().toList();

        for (int i = 0; i < courseDtos.size() - 1; i++) {
            CourseDto curr = courseDtos.get(i);
            CourseDto next = courseDtos.get(i + 1);
            if (sortField.equals("createdAt")) {
                assertThat(curr.getCreatedAt().isBefore(next.getCreatedAt())).isTrue();
            } else if (sortField.equals("title")) {
                assertThat(curr.getTitle().compareTo(next.getTitle()) < 0).isTrue();
            } else if (sortField.equals("updatedAt")) {
                assertThat(curr.getUpdatedAt().isBefore(next.getUpdatedAt())).isTrue();
            } else if (sortField.equals("id")) {
                assertThat(curr.getId().toString().compareTo(next.getId().toString()) < 0).isTrue();
            } else if (sortField.equals("price")) {
                assertThat(curr.getPrice() < next.getPrice()).isTrue();
            }
        }

    }

    // Direction works (parameterized test)
    @ParameterizedTest
    @CsvSource({ "ASC", "DESC" })
    public void courseTeacherController_getTeacherCourses_directionWorks(String direction) throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);
        prepareCourseUtil.prepareCourse(teacher);
        prepareCourseUtil.prepareCourse(teacher);
        prepareCourseUtil.prepareCourse(teacher);

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .param("direction", direction)
                        .param("sortField", "createdAt")
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<CourseDto> courseDtos = extractContent(res).getContent().stream().toList();

        for (int i = 0; i < courseDtos.size() - 1; i++) {
            CourseDto curr = courseDtos.get(i);
            CourseDto next = courseDtos.get(i + 1);
            if (direction.equals("ASC")) {
                assertThat(curr.getCreatedAt().isBefore(next.getCreatedAt())).isTrue();
            } else {
                assertThat(curr.getCreatedAt().isAfter(next.getCreatedAt())).isTrue();

            }
        }

    }

    // shoul throw error if invalid direction category passed
    @ParameterizedTest
    @CsvSource({ "INVALID,PROGRAMMING", "ASC,INVALID" })
    public void courseTeacherController_getTeacherCourses_shouldThrowErrorIfInvalidDirectionCategoryPassed(
            String direction, String category) throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses")
                        .param("category", category)
                        .param("direction", direction)
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    }

    // Should filtering by keyword works
    @ParameterizedTest
    @CsvSource({ "return_one", "return", "return_zerooo" })
    public void courseTeacherController_getTeacherCourses_shouldFilteringByKeywordWorks(String keyword)
            throws Exception {
        var teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var authToken = jwtUtils.generateToken(teacher);

        var course1 = CourseFactory.createPublishedCourse(teacher);
        course1.setTitle("return_one");
        course1.setPrice(2);
        courseRepository.save(course1);
        var course2 = CourseFactory.createPublishedCourse(teacher);
        course2.setTitle("return numb 2");
        courseRepository.save(course2);

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/teacher/courses").param("keyword", keyword)
                        .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<CourseDto> courseDtos = extractContent(res).getContent().stream().toList();
        if (keyword.equals("return_one")) {
            assertThat(courseDtos.size()).isEqualTo(1);
        } else if (keyword.equals("return")) {
            assertThat(courseDtos.size()).isEqualTo(2);
        } else {
            assertThat(courseDtos.size()).isEqualTo(0);
        }

    }

}
