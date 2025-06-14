package emil.find_course.IntegrationTests.course.coursePublic;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.CourseFactory;
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.course.SectionFactory;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.course.dto.pub.CourseDetailsPublicDto;
import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.user.entity.User;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CoursePublicControllerGetPublishedCourseTest extends IntegrationTestBase {

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

    @Test
    @DisplayName("Should sucessfully returns course with sections and chapters")
    public void coursePublicController_getPublishedCourse_shouldSucessfullyReturnsCourse() throws Exception {
        var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(3);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/public/courses/{courseId}", course.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var courseDto = objectMapper.readValue(result.getResponse().getContentAsString(), CourseDetailsPublicDto.class);

        assertThat(courseDto.getId()).isEqualTo(course.getId());
        assertThat(courseDto.getSections().size()).isEqualTo(3);
        assertThat(courseDto.getSections().get(0).getChapters().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should sucessfully returns course without chapters")
    public void coursePublicController_getPublishedCourse_shouldSucessfullyReturnsCourseWithoutChapters()
            throws Exception {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var course = CourseFactory.createPublishedCourse(teacher);
        var section = SectionFactory.createSection(course, 0);
        course.getSections().add(section);
        courseRepository.save(course);
        Course foundCourse = courseRepository.findAll().get(0);
        assertThat(foundCourse).isNotNull();
        assertThat(foundCourse.getSections().size()).isEqualTo(1);
        assertThat(foundCourse.getSections().get(0).getChapters()).isEmpty();

        MvcResult result = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/api/v1/public/courses/{courseId}", foundCourse.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var courseDto = objectMapper.readValue(result.getResponse().getContentAsString(), CourseDetailsPublicDto.class);

        assertThat(courseDto.getId()).isEqualTo(course.getId());
        assertThat(courseDto.getSections().size()).isEqualTo(1);
        assertThat(courseDto.getSections().get(0).getChapters()).isEmpty();
    }

    // sucessfully returns course without sections
    @Test
    @DisplayName("Should sucessfully returns course without sections")
    public void coursePublicController_getPublishedCourse_shouldSucessfullyReturnsCourseWithoutSections()
            throws Exception {
        var course = prepareCourseUtil.prepareCourse();
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/public/courses/{courseId}", course.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var courseDto = objectMapper.readValue(result.getResponse().getContentAsString(), CourseDetailsPublicDto.class);

        assertThat(courseDto.getId()).isEqualTo(course.getId());
        assertThat(courseDto.getSections()).isEmpty();
    }

    @Test
    @DisplayName("Should return 404 when no course found")
    public void coursePublicController_getPublishedCourse_shouldReturn404WhenNoCourseFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/courses/{courseId}", UUID.randomUUID().toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 when invalid id passed")
    public void coursePublicController_getPublishedCourse_shouldReturn400WhenInvalidIdPassed() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/public/courses/{courseId}", "invalid-uuid-format"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

}
