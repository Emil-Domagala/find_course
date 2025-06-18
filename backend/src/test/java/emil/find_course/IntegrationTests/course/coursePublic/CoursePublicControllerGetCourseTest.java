package emil.find_course.IntegrationTests.course.coursePublic;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
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
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.repository.CourseRepository;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CoursePublicControllerGetCourseTest extends IntegrationTestBase {

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

        // sucessfully returns one course
        @Test
        @DisplayName("Should sucessfully returns one course")
        public void coursePublicController_getPublishedCourse_shouldSucessfullyReturnsOneCourse() throws Exception {
                prepareCourseUtil.prepareCourses(1);

                mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1)).andReturn();

        }

        @Test
        @DisplayName("Should sucessfully returns multiple courses")
        public void coursePublicController_getPublishedCourse_shouldSucessfullyReturnsMultipleCourses()
                        throws Exception {
                prepareCourseUtil.prepareCourses(4);

                mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(4)).andReturn();

        }

        // Pagination works
        @ParameterizedTest(name = "Pagination works=> size {0}, total elements {1}, current page {2}")
        @CsvSource({
                        "5,10,0",
                        "10,10,0",
                        "5,10,1",
        })
        public void coursePublicController_getPublishedCourse_paginationWorks(String size, String totEle,
                        String currPage) throws Exception {
                prepareCourseUtil.prepareCourses(Integer.parseInt(totEle));

                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/courses")
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

        @ParameterizedTest(name = "Should sucessfully return sorted courses => {0}")
        @CsvSource({ "createdAt", "title", "updatedAt", "price", "id" })
        public void coursePublicController_getPublishedCourses_shouldSucessfullyReturnSortedCourses(String sortField)
                        throws Exception {
                var teacher = prepareTeacherUtil.prepareUniqueTeacher();
                var course1 = CourseFactory.createPublishedCourse(teacher);
                course1.setTitle("aaaa");
                course1.setPrice(1);
                var course2 = CourseFactory.createPublishedCourse(teacher);
                course2.setTitle("bbbb");
                course2.setPrice(2);
                var course3 = CourseFactory.createPublishedCourse(teacher);
                course3.setTitle("cccc");
                course3.setPrice(3);
                courseRepository.saveAll(List.of(course1, course2, course3));

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses").param("sortField", sortField))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                List<CourseDto> courses = extractContent(res).getContent().stream().toList();
                assertThat(courses.size()).isEqualTo(3);

                for (int i = 0; i < courses.size() - 1; i++) {
                        CourseDto curr = courses.get(i);
                        CourseDto next = courses.get(i + 1);

                        if (sortField.equals("createdAt")) {
                                assertThat(curr.getCreatedAt().isBefore(next.getCreatedAt())).isTrue();
                        } else if (sortField.equals("title")) {
                                assertThat(curr.getTitle().compareTo(next.getTitle()) < 0).isTrue();
                        } else if (sortField.equals("updatedAt")) {
                                assertThat(curr.getUpdatedAt().isBefore(next.getUpdatedAt())).isTrue();
                        } else if (sortField.equals("price")) {
                                assertThat(curr.getPrice() < next.getPrice()).isTrue();
                        } else if (sortField.equals("id")) {
                                assertThat(curr.getId().toString().compareTo(next.getId().toString()) < 0).isTrue();
                        }
                }

        }

        @Test
        @DisplayName("Should return empty content if no course after keyword found")
        public void coursePublicController_getPublishedCourses_shouldReturnEmptyContentIfNoCourseAfterKeywordFound()
                        throws Exception {
                prepareCourseUtil.prepareCourses(3);

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses").param("keyword",
                                                Instant.now().toString()))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                List<CourseDto> courses = extractContent(res).getContent().stream().toList();
                assertThat(courses.size()).isEqualTo(0);
        }

        @ParameterizedTest(name = "Should return only courses with proper keyword => title: {0} keyword: {1}")
        @CsvSource({ "title,tit", "title,title" })
        public void coursePublicController_getPublishedCourses_shouldReturnOnlyCoursesWithProperKeyword(String title,
                        String keyword)
                        throws Exception {
                var teacher = prepareTeacherUtil.prepareUniqueTeacher();
                var course = CourseFactory.createPublishedCourse(teacher);
                course.setTitle(title);
                var course2 = CourseFactory.createPublishedCourse(teacher);
                courseRepository.saveAll(List.of(course, course2));

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses").param("keyword", keyword))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                List<CourseDto> courses = extractContent(res).getContent().stream().toList();
                assertThat(courses.size()).isEqualTo(1);
                CourseDto courseDto = courses.get(0);
                assertThat(courseDto.getTitle()).isEqualTo(title);
        }

        @Test
        @DisplayName("Should return all courses if keyword is empty string")
        public void coursePublicController_getPublishedCourses_shouldReturnAllCoursesIfKeywordIsEmptyString()
                        throws Exception {
                prepareCourseUtil.prepareCourses(3);

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses").param("keyword", ""))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                var content = extractContent(res);
                assertThat(content.getContent().size()).isEqualTo(3);

        }

        @Test
        @DisplayName("Should return only courses with proper category")
        public void coursePublicController_getPublishedCourses_shouldReturnCoursesWithProperCategory()
                        throws Exception {

                var teacher = prepareTeacherUtil.prepareUniqueTeacher();
                var course2 = CourseFactory.createPublishedCourse(teacher);
                course2.setCategory(CourseCategory.PROGRAMMING);
                var course = CourseFactory.createPublishedCourse(teacher);
                course.setCategory(CourseCategory.PROJECT_MANAGEMENT);
                courseRepository.saveAll(List.of(course, course2));

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses")
                                                .param("category", CourseCategory.PROJECT_MANAGEMENT.toString()))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                var content = extractContent(res);
                assertThat(content.getContent().size()).isEqualTo(1);

        }

        @ParameterizedTest(name = "Should sucessfully reverse order => {0}")
        @CsvSource({ "ASC", "DESC" })
        public void coursePublicController_getPublishedCourses_shouldSucessfullyReverseOrder(String direction)
                        throws Exception {
                int count = 3;
                prepareCourseUtil.prepareCourses(count);

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses")
                                                .param("sortField", "createdAt")
                                                .param("direction", direction))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.page")
                                                .value(PaginationRequest.DEFAULT_PAGE + 1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.size")
                                                .value(PaginationRequest.DEFAULT_SIZE))
                                .andReturn();
                PagingResult<CourseDto> content = extractContent(res);
                List<CourseDto> courses = content.getContent().stream().toList();

                // is ASC and createdAt
                for (int i = 0; i < count - 1; i++) {
                        Instant current = courses.get(i).getCreatedAt();
                        Instant next = courses.get(i + 1).getCreatedAt();
                        if ("ASC".equals(direction)) {
                                assertThat(current.isBefore(next)).isTrue();
                        } else {
                                assertThat(current.isAfter(next)).isTrue();
                        }
                }

        }

        // ERROR: invalid direction
        @Test
        @DisplayName("Should throw 400 if invalid direction")
        public void oursePublicController_getPublishedCourses_shouldThrow400IfInvalidDirection() throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses").param("direction",
                                                "invaidDirection"))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        }

        // ERROR: invalid CourseCategory
        @Test
        @DisplayName("Should throw 400 if invalid CourseCategory")
        public void coursePublicController_getPublishedCourses_shouldThrow400IfInvalidCourseCategory()
                        throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses").param("category",
                                                "invalidCategory"))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        }

        @Test
        @DisplayName("Should Return 200 For Invalid types that gets to default")
        public void coursePublicController_getPublishedCourse_shouldReturn200ForInvalidInputsThatGetsToDefault()
                        throws Exception {

                int count = 3;
                prepareCourseUtil.prepareCourses(3);

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses")
                                                .param("page", "-1")
                                                .param("size", "-5")
                                                .param("sortField", "unknownField"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(
                                                PaginationRequest.DEFAULT_PAGE + 1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(
                                                PaginationRequest.DEFAULT_SIZE))
                                .andReturn();
                PagingResult<CourseDto> content = extractContent(res);
                List<CourseDto> courses = content.getContent().stream().toList();

                // is ASC and createdAt
                for (int i = 0; i < count - 1; i++) {
                        Instant current = courses.get(i).getCreatedAt();
                        Instant next = courses.get(i + 1).getCreatedAt();
                        assertThat(current.isBefore(next)).isTrue();
                }

        }

        @Test
        @DisplayName("Should returns to default page, size, sortField and direction if not passed")
        public void coursePublicController_getPublishedCourses_shouldReturnsToDefaultPageSizeSortFieldAndDirection()
                        throws Exception {
                int count = 3;
                prepareCourseUtil.prepareCourses(3);

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses"))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                PagingResult<CourseDto> content = extractContent(res);
                assertThat(content.getTotalElements()).isEqualTo(count);
                assertThat(content.getTotalPages())
                                .isEqualTo((int) Math.ceil(count / PaginationRequest.DEFAULT_SIZE) + 1);
                assertThat(content.getContent().size()).isEqualTo(count);
                assertThat(content.getPage()).isEqualTo(PaginationRequest.DEFAULT_PAGE + 1);
                assertThat(content.getSize()).isEqualTo(PaginationRequest.DEFAULT_SIZE);
                List<CourseDto> courses = content.getContent().stream().toList();

                // is ASC and createdAt
                for (int i = 0; i < count - 1; i++) {
                        Instant current = courses.get(i).getCreatedAt();
                        Instant next = courses.get(i + 1).getCreatedAt();
                        assertThat(current.isBefore(next)).isTrue();
                }

        }

        @Test
        @DisplayName("Should return empty content")
        public void coursePublicController_getPublishedCourses_shouldReturnEmptyContent() throws Exception {

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/public/courses"))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                PagingResult<CourseDto> content = extractContent(res);
                assertThat(content.getTotalElements()).isEqualTo(0);
                assertThat(content.getTotalPages()).isEqualTo(0);
                assertThat(content.getContent()).isEmpty();
        }

}
