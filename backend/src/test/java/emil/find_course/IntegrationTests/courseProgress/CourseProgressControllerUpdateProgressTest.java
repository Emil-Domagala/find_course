package emil.find_course.IntegrationTests.courseProgress;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.courseProgress.dto.request.UpdateProgressRequest;
import emil.find_course.courseProgress.repository.ChapterProgressRepository;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseProgressControllerUpdateProgressTest extends IntegrationTestBase {
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
        private ChapterProgressRepository chapterProgressRepository;

        @Test
        public void courseProgressController_updateProgress_shouldReturn404IfNoCourseProgressFound() throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var token = jwtUtils.generateToken(user);
                var req = UpdateProgressRequest.builder().chapterProgressId(UUID.randomUUID()).build();
                var course = prepareCourseWithStudentUtil.prepareCourse(user, 2);
                var json = objectMapper.writeValueAsString(req);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/progress/{courseId}", course.getId())
                                .contentType("application/json")
                                .content(json)
                                .cookie(new Cookie(authCookieName, token)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        // Invalid UpdateProgressRequest
        @ParameterizedTest(name = "invalid UpdateProgressRequest => {0}")
        @CsvSource({
                        "{\"chapterProgressId\":\"550e8400-e29b-41d4-a716-446655440000\",\"completed\":\"INVALID\"}",
                        "{\"chapterProgressId\":\"updatedChapterId.toString()\",\"completed\":true}",
                        "{\"chapterProgressId\":\"550e8400-e29b-41d4-a716-446655440000\",\"completed\":null}",
                        "{\"chapterProgressId\":null,\"completed\":true}" })
        public void courseProgressController_updateProgress_shouldReturn400(String json) throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
                var token = jwtUtils.generateToken(user);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/progress/{courseId}", course.getId())
                                .contentType("application/json")
                                .content(json)
                                .cookie(new Cookie(authCookieName, token)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        // update chapter progress true/false
        @ParameterizedTest(name = "updates chapter progress to: {0}")
        @CsvSource({ "true", "false" })
        public void courseProgressController_updateProgress_shouldReturn200(boolean completed) throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
                var courseProgress = prepareCourseProgressUtil.createCourseProgress(course, user);
                var token = jwtUtils.generateToken(user);
                UUID updatedChapterId = courseProgress.getSections().get(0).getChapters().get(0).getId();
                entityManager.flush();
                entityManager.clear();

                var req = UpdateProgressRequest.builder().chapterProgressId(updatedChapterId).completed(completed)
                                .build();
                var json = objectMapper.writeValueAsString(req);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/progress/{courseId}", course.getId())
                                .contentType("application/json")
                                .content(json)
                                .cookie(new Cookie(authCookieName, token)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                assertThat(chapterProgressRepository.findById(updatedChapterId).get().isCompleted() == completed)
                                .isTrue();
        }

        // Cant update other user chapter progress
        @Test
        public void courseProgressController_updateProgress_shouldReturn404IftryingToUpdateOtherUsersProgress()
                        throws Exception {
                var user = prepareUserUtil.prepareVerifiedUser();
                var course = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
                var courseProgress = prepareCourseProgressUtil.createCourseProgress(course, user);
                UUID updatedChapterId = courseProgress.getSections().get(0).getChapters().get(0).getId();
                entityManager.flush();
                entityManager.clear();

                var req = UpdateProgressRequest.builder().chapterProgressId(updatedChapterId).completed(true)
                                .build();
                var json = objectMapper.writeValueAsString(req);

                var token = jwtUtils.generateToken(prepareUserUtil.prepareVerifiedUser("email@rmail.com", "Name"));
                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/progress/{courseId}", course.getId())
                                .contentType("application/json")
                                .content(json)
                                .cookie(new Cookie(authCookieName, token)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
}
