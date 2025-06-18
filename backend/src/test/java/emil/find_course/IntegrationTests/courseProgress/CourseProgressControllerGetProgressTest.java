package emil.find_course.IntegrationTests.courseProgress;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.ChapterFactory;
import emil.find_course.IntegrationTests.course.SectionFactory;
import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.chapter.repository.ChapterRepository;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.course.section.repository.SectionRepository;
import emil.find_course.courseProgress.CourseProgressService;
import emil.find_course.courseProgress.dto.CourseProgressDto;
import emil.find_course.courseProgress.repository.ChapterProgressRepository;
import emil.find_course.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseProgressControllerGetProgressTest extends IntegrationTestBase {
    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private PrepareCourseWithStudentUtil prepareCourseWithStudentUtil;

    @Autowired
    private PrepareCourseProgressUtil prepareCourseProgressUtil;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ChapterProgressRepository chapterProgressRepository;

    @MockitoSpyBean
    private CourseProgressService courseProgressService;

    User user;
    String token;

    @BeforeEach
    public void setup() {
        user = prepareUserUtil.prepareVerifiedUser();
        token = jwtUtils.generateToken(user);
    }

    private CourseProgressDto extractContent(MvcResult res) throws Exception {
        String content = res.getResponse().getContentAsString();
        return objectMapper.readValue(content, CourseProgressDto.class);
    }

    @Test
    public void courseProgressController_getProgress_shouldSucessfullySynchCourseProgress() throws Exception {

        var c1 = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        var cp1 = prepareCourseProgressUtil.createCourseProgress(c1, user);
        var chpC = cp1.getSections().get(0).getChapters().get(0);
        chpC.setCompleted(true);
        chapterProgressRepository.save(chpC);

        entityManager.flush();
        entityManager.clear();

        var s3 = SectionFactory.createSection(c1, 3);
        var ch3_3 = ChapterFactory.createChapterWithContent(3, s3);
        s3.getChapters().add(ch3_3);
        c1.getSections().add(s3);
        c1.setUpdatedAt(Instant.now());

        courseRepository.save(c1);
        entityManager.flush();
        entityManager.clear();

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progress/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractContent(res);
        assertThat(result.getSections().size()).isEqualTo(3);
        assertThat(result.getSections().get(0).getChapters().get(0).isCompleted()).isTrue();
        assertThat(result.getSections().get(2).getChapters().get(0).isCompleted()).isFalse();
    }

    @Test
    public void courseProgressController_getProgress_shouldSucessfullyDeleteCourseProgressIfChapterWasDeleted()
            throws Exception {

        var c1 = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        prepareCourseProgressUtil.createCourseProgress(c1, user);
        var lastChapterId = c1.getSections().get(0).getChapters().get(1).getId();

        var chForDeletion = c1.getSections().get(0).getChapters().get(0);
        var secForDeletion = c1.getSections().get(1);

        entityManager.flush();
        entityManager.clear();

        var c2 = courseRepository.findById(c1.getId()).get();
        c2.setUpdatedAt(Instant.now());
        c2.getSections().remove(secForDeletion);
        c2.getSections().get(0).getChapters().remove(chForDeletion);
        courseRepository.save(c2);

        entityManager.flush();
        entityManager.clear();

        assertThat(sectionRepository.count() == 1).isTrue();
        assertThat(chapterRepository.count() == 1).isTrue();

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progress/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractContent(res);

        assertThat(result.getSections().size()).isEqualTo(1);
        assertThat(result.getSections().get(0).getChapters().size()).isEqualTo(1);
        var oryginalChapterId = result.getSections().get(0).getChapters().get(0).getOriginalChapter().getId();
        assertThat(lastChapterId).isEqualByComparingTo(oryginalChapterId);
    }

    @Test
    public void courseProgressController_getProgress_shouldSucessfullyCreateNewCourseProgress() throws Exception {

        var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        prepareCourseProgressUtil.createCourseProgress(course, user);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progress/{courseId}", course.getId())
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extractContent(res);
        System.out.println(result.toString());
        assertThat(result.getSections().size()).isEqualTo(2);
        assertThat(result.getSections().get(0).getChapters().size()).isEqualTo(2);
        assertThat(result.getSections().get(1).getChapters().size()).isEqualTo(2);
        var chProgS1Ch1 = result.getSections().get(1).getChapters().get(1);
        var chapterS1Ch1 = course.getSections().get(1).getChapters().get(1);
        assertThat(chProgS1Ch1.getOriginalChapter().getId()).isEqualTo(chapterS1Ch1.getId());

        // check if is in order:
        for (int i = 0; i < result.getSections().size(); i++) {
            var secProg = result.getSections().get(i);
            var sec = course.getSections().get(i);
            assertThat(sec.getPosition()).isEqualTo(i);
            assertThat(secProg.getOriginalSection().getId()).isEqualTo(sec.getId());
            for (int j = 0; j < secProg.getChapters().size(); j++) {
                var chProg = secProg.getChapters().get(j);
                var ch = course.getSections().get(i).getChapters().get(j);
                assertThat(ch.getPosition()).isEqualTo(j);
                assertThat(chProg.getOriginalChapter().getId()).isEqualTo(ch.getId());
            }
        }

    }

    // 400 invalid coursId
    @Test
    public void courseProgressController_getProgress_shouldReturn400IfInvalidCourseId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progress/{courseId}", "course.getId()")
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    // user not enrolled
    @Test
    public void courseProgressController_getProgress_shouldReturn403IfUserNotEnrolled() throws Exception {
        var u2 = prepareUserUtil.prepareVerifiedUser("email@rmail.com", "Name");
        var c1 = prepareCourseWithStudentUtil.prepareCourseWithChapters(u2, 2);
        prepareCourseProgressUtil.createCourseProgress(c1, user);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/progress/{courseId}", c1.getId())
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();
    }

}
