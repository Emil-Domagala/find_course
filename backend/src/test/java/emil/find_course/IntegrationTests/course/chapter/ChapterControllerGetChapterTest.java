package emil.find_course.IntegrationTests.course.chapter;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.chapter.dto.prot.ChapterProtectedDto;
import emil.find_course.user.entity.User;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ChapterControllerGetChapterTest extends IntegrationTestBase {
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
        private PrepareCourseUtil prepareCourseUtil;

        User user;
        String authToken;

        @BeforeEach
        public void setup() {
                user = prepareUserUtil.prepareVerifiedUser();
                authToken = jwtUtils.generateToken(user);
        }

        @Test
        @DisplayName("Should sucessfully returns one chapter")
        public void chapterController_getChapter_shouldSucessfullyReturnsOneChapter() throws Exception {
                var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
                var chapter = course.getSections().get(0).getChapters().get(0);
                UUID chapterId = chapter.getId();
                UUID courseId = course.getId();

                MvcResult res = mockMvc.perform(
                                MockMvcRequestBuilders
                                                .get("/api/v1/student/courses/{courseId}/chapters/{chapterId}",
                                                                courseId, chapterId)
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                ChapterProtectedDto chapterDto = objectMapper.readValue(res.getResponse().getContentAsString(),
                                ChapterProtectedDto.class);
                assertThat(chapterDto.getId()).isEqualTo(chapterId);

        }

        // cant access chapter if i dont own course
        @Test
        @DisplayName("Should sucessfully returns one chapter")
        public void chapterController_getChapter_shouldReturn404IfCantAccessChapter() throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(2);
                var chapter = course.getSections().get(0).getChapters().get(0);
                UUID chapterId = chapter.getId();
                UUID courseId = course.getId();

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .get("/api/v1/student/courses/{courseId}/chapters/{chapterId}",
                                                                courseId, chapterId)
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();

        }

        // null if i own course but there is no chapter that i specified
        @Test
        @DisplayName("Should return 404 if chapter do not exists")
        public void chapterController_getChapter_shouldReturn404IfChapterDoNotExists() throws Exception {
                var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);

                UUID courseId = course.getId();

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .get("/api/v1/student/courses/{courseId}/chapters/{chapterId}",
                                                                courseId, UUID.randomUUID())
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
        }

        // didnt found course
        @Test
        @DisplayName("Should return 404 if course do not exists")
        public void chapterController_getChapter_shouldReturn404IfCourseDoNotExists() throws Exception {
                var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
                var chapter = course.getSections().get(0).getChapters().get(0);
                UUID chapterId = chapter.getId();

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .get("/api/v1/student/courses/{courseId}/chapters/{chapterId}",
                                                                UUID.randomUUID(), chapterId)
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
        }

        // invalid uuid course or chapter or both
        @ParameterizedTest(name = "Invalid inputs => courseId: {0}, chapterId: {1}")
        @CsvSource({ "valid, INVALID", "INVALID, valid", "INVALID, INVALID" })
        public void chapterController_getChapter_shouldReturn400IfInvalidInputs(String courseId, String chapterId)
                        throws Exception {
                if (chapterId.equals("valid")) {
                        chapterId = UUID.randomUUID().toString();
                }
                if (courseId.equals("valid")) {
                        courseId = UUID.randomUUID().toString();
                }
                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .get("/api/v1/student/courses/{courseId}/chapters/{chapterId}",
                                                                courseId, chapterId)
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        }

        // not logged in
        @Test
        @DisplayName("Should return 401 if not loggedIn")
        public void chapterController_getChapter_shouldReturn401IfNotLoggedIn() throws Exception {
                var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
                var chapter = course.getSections().get(0).getChapters().get(0);
                UUID chapterId = chapter.getId();
                UUID courseId = course.getId();

                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/courses/{courseId}/chapters/{chapterId}",
                                courseId, chapterId))
                                .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
        }

}
