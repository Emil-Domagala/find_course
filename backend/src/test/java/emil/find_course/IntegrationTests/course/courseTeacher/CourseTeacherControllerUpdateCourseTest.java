package emil.find_course.IntegrationTests.course.courseTeacher;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.PrepareCourseUtil;
import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.course.chapter.dto.request.ChapterRequest;
import emil.find_course.course.chapter.enums.ChapterType;
import emil.find_course.course.chapter.repository.ChapterRepository;
import emil.find_course.course.dto.request.CourseRequest;
import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.enums.Level;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.course.section.dto.request.SectionRequest;
import emil.find_course.course.section.entity.Section;
import emil.find_course.course.section.repository.SectionRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseTeacherControllerUpdateCourseTest extends IntegrationTestBase {

        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Autowired
        private JwtUtils jwtUtils;
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private CourseRepository courseRepository;
        @Autowired
        private ChapterRepository chapterRepository;
        @Autowired
        private SectionRepository sectionRepository;
        @Autowired
        private PrepareTeacherUtil prepareTeacherUtil;
        @Autowired
        private PrepareUserUtil prepareUserUtil;
        @Autowired
        private PrepareCourseUtil prepareCourseUtil;
        @Autowired
        private PrepareCourseWithStudentUtil prepareCourseWithStudentUtil;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private EntityManager entityManager;

        private MockMultipartFile createCourseDataPart(CourseRequest courseRequest) throws Exception {
                String json = objectMapper.writeValueAsString(courseRequest);
                return new MockMultipartFile("courseData", null, MediaType.APPLICATION_JSON_VALUE, json.getBytes());
        }

        // Happy path:
        // -- course
        // Partial and full course update (without sections)
        private static Stream<Arguments> prepareUpdateCourseRequest() {
                CourseRequest requestTitle = CourseRequest.builder().title("Valid Title").build();
                CourseRequest requestDesc = CourseRequest.builder().description("Valid desc").build();
                CourseRequest requestCat = CourseRequest.builder()
                                .description(CourseCategory.PROJECT_MANAGEMENT.toString())
                                .build();
                CourseRequest requestPrice = CourseRequest.builder().price(100).build();
                CourseRequest requestLevel = CourseRequest.builder().level(Level.ADVANCED).build();
                CourseRequest requestStatus = CourseRequest.builder().status(CourseStatus.PUBLISHED).build();
                CourseRequest requestFull = CourseRequest.builder().title("Valid Title").description("Valid desc")
                                .category(CourseCategory.PROJECT_MANAGEMENT).price(100).level(Level.ADVANCED)
                                .status(CourseStatus.PUBLISHED).build();

                return Stream.of(
                                Arguments.of(requestTitle),
                                Arguments.of(requestDesc),
                                Arguments.of(requestCat),
                                Arguments.of(requestPrice),
                                Arguments.of(requestLevel),
                                Arguments.of(requestStatus),
                                Arguments.of(requestFull));
        }

        @ParameterizedTest
        @MethodSource("prepareUpdateCourseRequest")
        @DisplayName("Should sucessfully update course")
        public void courseTeacherController_updateCourse_shouldSucessfullyUpdateCourse(CourseRequest request)
                        throws Exception {
                Course course = prepareCourseUtil.prepareCourse();
                request.setId(course.getId());
                var authToken = jwtUtils.generateToken(course.getTeacher());
                var req = createCourseDataPart(request);

                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                var updatedCourse = courseRepository.findById(course.getId()).get();

                if (request.getTitle() != null) {
                        assertThat(updatedCourse.getTitle()).isEqualTo(request.getTitle());
                }
                if (request.getDescription() != null) {
                        assertThat(updatedCourse.getDescription()).isEqualTo(request.getDescription());
                }
                if (request.getCategory() != null) {
                        assertThat(updatedCourse.getCategory()).isEqualTo(request.getCategory());
                }
                if (request.getPrice() != null) {
                        assertThat(updatedCourse.getPrice()).isEqualTo(request.getPrice());
                }
                if (request.getLevel() != null) {
                        assertThat(updatedCourse.getLevel()).isEqualTo(request.getLevel());
                }
                if (request.getStatus() != null) {
                        assertThat(updatedCourse.getStatus()).isEqualTo(request.getStatus());
                }

        }

        // Deletes all sections and chapters if not provided
        @Test
        @DisplayName("Should sucessfully delete Sections and Chapters if not provided")
        public void TeacherController_updateCourse_shouldSucessfullyDeleteSectionsAndChaptersIfNotProvided()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(2);

                var authToken = jwtUtils.generateToken(course.getTeacher());
                CourseRequest request = CourseRequest.builder().id(course.getId()).build();
                var req = createCourseDataPart(request);

                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                assertThat(sectionRepository.count()).isEqualTo(0);
                assertThat(chapterRepository.count()).isEqualTo(0);

        }

        // Happy path:
        // --section
        // Partial and full section update (without chapters)
        private static Stream<Arguments> prepareUpdateSectionRequest() {
                SectionRequest requestTitle = SectionRequest.builder().title("Valid Title").build();
                SectionRequest requestDesc = SectionRequest.builder().description("Valid desc").build();
                return Stream.of(Arguments.of(requestTitle), Arguments.of(requestDesc));
        }

        @ParameterizedTest
        @MethodSource("prepareUpdateSectionRequest")
        public void courseTeacherController_updateCourse_shouldSucessfullyUpdateSection(SectionRequest request)
                        throws Exception {
                Course course = prepareCourseUtil.prepareCourseWithSections(1);
                request.setId(course.getSections().get(0).getId());
                CourseRequest courseRequest = CourseRequest.builder().id(course.getId()).sections(List.of(request))
                                .build();
                var req = createCourseDataPart(courseRequest);
                var authToken = jwtUtils.generateToken(course.getTeacher());

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                var updatedSection = sectionRepository.findById(request.getId()).get();
                if (request.getTitle() != null) {
                        assertThat(updatedSection.getTitle()).isEqualTo(request.getTitle());
                }
                if (request.getDescription() != null) {
                        assertThat(updatedSection.getDescription()).isEqualTo(request.getDescription());
                }
                assertThat(updatedSection.getPosition()).isEqualTo(0);
        }

        // Deletes all chapters if not provided
        @Test
        @DisplayName("Should sucessfully delete Chapters if not provided")
        public void TeacherController_updateCourse_shouldSucessfullyDeleteChaptersIfNotProvided() throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(2);
                var authToken = jwtUtils.generateToken(course.getTeacher());
                SectionRequest sRequest = SectionRequest.builder().id(course.getSections().get(0).getId()).build();
                CourseRequest cRequest = CourseRequest.builder().id(course.getId()).sections(List.of(sRequest)).build();
                var req = createCourseDataPart(cRequest);
                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                assertThat(sectionRepository.count()).isEqualTo(1);
                assertThat(chapterRepository.count()).isEqualTo(0);
        }

        // Sucessfully add new sections
        @Test
        @DisplayName("Should sucessfully add new sections")
        public void courseTeacherController_updateCourse_shouldSucessfullyAddNewSections() throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                var authToken = jwtUtils.generateToken(course.getTeacher());
                SectionRequest sRequest = SectionRequest.builder().tempId("temp_validId").title("Valid title")
                                .description("valid desc").build();
                CourseRequest cRequest = CourseRequest.builder().id(course.getId()).sections(List.of(sRequest)).build();
                var req = createCourseDataPart(cRequest);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                assertThat(sectionRepository.count()).isEqualTo(1);

        }

        @Test
        @DisplayName("Should sucessfully add new section if one exists")
        public void courseTeacherController_updateCourse_shouldSucessfullyAddNewSectionIfOneExists() throws Exception {
                // Sucessfully updates existing section add new and change position and also
                // sections are returned in pos order

                var course = prepareCourseUtil.prepareCourseWithSections(1);
                var authToken = jwtUtils.generateToken(course.getTeacher());
                SectionRequest sRequest = SectionRequest.builder().tempId("temp_validId").title("Should_be_pos_0")
                                .description("valid desc").build();
                SectionRequest sRequest2 = SectionRequest.builder().id(course.getSections().get(0).getId())
                                .title("Should_be_pos_1").build();
                CourseRequest cRequest = CourseRequest.builder().id(course.getId()).sections(List.of(sRequest,
                                sRequest2)).build();

                var req = createCourseDataPart(cRequest);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                assertThat(sectionRepository.count()).isEqualTo(2);
                Course foundCourse = courseRepository.findById(course.getId()).get();
                var sec0 = foundCourse.getSections().get(0);
                var sec1 = foundCourse.getSections().get(1);
                assertThat(sec0.getTitle()).isEqualTo("Should_be_pos_0");
                assertThat(sec1.getTitle()).isEqualTo("Should_be_pos_1");
                assertThat(sec1.getId()).isEqualTo(sRequest2.getId());
                assertThat(sec0.getPosition()).isEqualTo(0);
                assertThat(sec1.getPosition()).isEqualTo(1);

        }

        // -- chapters
        // Partial and full chapter update
        private static Stream<Arguments> prepareUpdateChapterRequest() {
                ChapterRequest requestTitle = ChapterRequest.builder().title("Valid Title").build();
                ChapterRequest requestCont = ChapterRequest.builder().content("Valid desc").build();
                ChapterRequest requestVideoUrl = ChapterRequest.builder().videoUrl("https://placehold.co/600x400")
                                .build();
                return Stream.of(Arguments.of(requestTitle), Arguments.of(requestCont), Arguments.of(requestVideoUrl));
        }

        @ParameterizedTest
        @MethodSource("prepareUpdateChapterRequest")
        public void courseTeacherController_updateCourse_shouldSucessfullyUpdateChapter(ChapterRequest request)
                        throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(1);
                UUID cId = course.getId(), sId = course.getSections().get(0).getId(),
                                chId = course.getSections().get(0).getChapters().get(0).getId();

                var authToken = jwtUtils.generateToken(course.getTeacher());

                request.setId(chId);
                var sRequest = SectionRequest.builder().id(sId).chapters(List.of(request)).build();
                var cRequest = CourseRequest.builder().id(cId).sections(List.of(sRequest)).build();
                var req = createCourseDataPart(cRequest);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();
                var updatedChapter = chapterRepository.findById(chId).get();
                if (request.getTitle() != null) {
                        assertThat(updatedChapter.getTitle()).isEqualTo(request.getTitle());
                }
                if (request.getContent() != null) {
                        assertThat(updatedChapter.getContent()).isEqualTo(request.getContent());
                        assertThat(updatedChapter.getType()).isEqualTo(ChapterType.TEXT);
                }
                if (request.getVideoUrl() != null) {
                        assertThat(updatedChapter.getVideoUrl()).isEqualTo(request.getVideoUrl());
                        assertThat(updatedChapter.getType()).isEqualTo(ChapterType.VIDEO);
                }
        }

        // Sucessfully add new chapters
        @Test
        @DisplayName("Should sucessfully add new chapters")
        public void courseTeacherController_updateCourse_shouldSucessfullyAddNewChapters() throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSections(1);
                var authToken = jwtUtils.generateToken(course.getTeacher());

                ChapterRequest chReq = ChapterRequest.builder().tempId("authCookieName").title("valid title")
                                .content("valid content").build();
                SectionRequest sReq = SectionRequest.builder().id(course.getSections().get(0).getId())
                                .chapters(List.of(chReq))
                                .build();
                CourseRequest cReq = CourseRequest.builder().id(course.getId()).sections(List.of(sReq)).build();
                var req = createCourseDataPart(cReq);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                assertThat(chapterRepository.count()).isEqualTo(1);
        }
        // Sucessfully updates existing chapter add new and change position
        // and also chapters are returned in pos order

        @Test
        @DisplayName("Should sucessfully add new chapter if one exists")
        public void courseTeacherController_updateCourse_shouldSucessfullyAddNewChapterIfOneExists() throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(1);
                var authToken = jwtUtils.generateToken(course.getTeacher());
                UUID cId = course.getId(), sId = course.getSections().get(0).getId(),
                                chId = course.getSections().get(0).getChapters().get(0).getId();

                var chR = ChapterRequest.builder().tempId("authCookieName").title("Should_be_pos_0")
                                .content("valid content")
                                .build();
                var chR1 = ChapterRequest.builder().id(chId).title("Should_be_pos_1").build();
                var sR = SectionRequest.builder().id(sId).chapters(List.of(chR, chR1)).build();
                var cR = CourseRequest.builder().id(cId).sections(List.of(sR)).build();
                var req = createCourseDataPart(cR);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                assertThat(chapterRepository.count()).isEqualTo(2);
                Section sFound = sectionRepository.findById(sId).get();
                var ch0 = sFound.getChapters().get(0);
                var ch1 = sFound.getChapters().get(1);
                assertThat(ch0.getTitle()).isEqualTo("Should_be_pos_0");
                assertThat(ch1.getTitle()).isEqualTo("Should_be_pos_1");
                assertThat(ch1.getId()).isEqualTo(chR1.getId());
        }

        // !!! DELETES ONLY SECTIONS AND CHAPTERS THAT WAS NOT PROVIDED
        @Test
        @DisplayName("Should sucessfully delete sections and chapters if not provided")
        public void courseTeacherController_updateCourse_shouldDeleteSectionsAndChaptersIfNotProvided()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(2);
                var authToken = jwtUtils.generateToken(course.getTeacher());
                Section section1 = course.getSections().get(0);

                UUID cId = course.getId(), sId0 = section1.getId(), chId0_0 = section1.getChapters().get(0).getId();

                var chR0_0 = ChapterRequest.builder().id(chId0_0).build();
                var sR0 = SectionRequest.builder().id(sId0).chapters(List.of(chR0_0)).build();
                var cR = CourseRequest.builder().id(cId).sections(List.of(sR0)).build();
                var req = createCourseDataPart(cR);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                assertThat(sectionRepository.count()).isEqualTo(1);
                assertThat(chapterRepository.count()).isEqualTo(1);

        }

        // !! Sucessfully adds multiple sections and chapters
        @Test
        @DisplayName("Should sucessfully add multiple sections and chapters")
        public void courseTeacherController_updateCourse_shouldSucessfullyAddMultipleSectionsAndChapters()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSectionsAndChapters(2);
                var authToken = jwtUtils.generateToken(course.getTeacher());
                List<SectionRequest> sections = new ArrayList<>();
                int total = 2;
                for (int i = 0; i < total; i++) {
                        List<ChapterRequest> chapters = new ArrayList<>();
                        for (int j = 0; j < total; j++) {
                                var ch = ChapterRequest.builder().tempId("authCookieName").title("Should_be_pos_" + j)
                                                .content("valid content").build();
                                chapters.add(ch);
                        }
                        var s = SectionRequest.builder().tempId("temp_validId").title("Should_be_pos" + i)
                                        .description("valid desc").build();
                        s.setChapters(chapters);
                        sections.add(s);
                }
                var cReq = CourseRequest.builder().id(course.getId()).sections(sections).build();
                var req = createCourseDataPart(cReq);
                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                assertThat(chapterRepository.count()).isEqualTo(total * total);
                assertThat(sectionRepository.count()).isEqualTo(total);

        }

        // Auth tests
        @Test
        @DisplayName("Should return 403 if User is not course teacher")
        public void courseTeacherController_updateCourse_shouldReturn403IfUserIsNotCourseTeacher() throws Exception {
                var course = prepareCourseUtil.prepareCourse();

                var authToken = jwtUtils.generateToken(prepareTeacherUtil.prepareTeacher());
                CourseRequest request = CourseRequest.builder().id(course.getId()).build();
                var req = createCourseDataPart(request);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        @DisplayName("Should return 403 if user is not a teacher")
        public void courseTeacherController_updateCourse_shouldReturn403IfUserIsNotTeacher() throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                var authToken = jwtUtils.generateToken(prepareUserUtil.prepareVerifiedUser());
                CourseRequest request = CourseRequest.builder().id(course.getId()).build();
                var req = createCourseDataPart(request);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        @DisplayName("Should return 499 if no auth token passed")
        public void courseTeacherController_updateCourse_shouldReturn499IfNoAuthTokenPassed() throws Exception {
                var course = prepareCourseUtil.prepareCourse();

                CourseRequest request = CourseRequest.builder().id(course.getId()).build();
                var req = createCourseDataPart(request);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req))
                                .andExpect(MockMvcResultMatchers.status().is(499));
        }

        @Test
        @DisplayName("Should return 498 if Invalid auth token")
        public void courseTeacherController_updateCourse_shouldReturn498IfInvalidAuthTokenPassed() throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                CourseRequest request = CourseRequest.builder().id(course.getId()).build();
                var req = createCourseDataPart(request);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                                course.getId())
                                                .file(req).cookie(new Cookie(authCookieName, "authToken")))
                                .andExpect(MockMvcResultMatchers.status().is(498));
        }

        // Validation course ---------

        private static Stream<Arguments> prepareInvalidCourseRequestInputs() {
                CourseRequest invalidTitleLong = CourseRequest.builder().title("a".repeat(101)).build();
                CourseRequest invalidTitleShort = CourseRequest.builder().title("a").build();
                CourseRequest invalidDescLong = CourseRequest.builder().description("a".repeat(1001)).build();
                CourseRequest invalidDescShort = CourseRequest.builder().description("a").build();
                CourseRequest invalidPriceNegative = CourseRequest.builder().price(-1).build();
                return Stream.of(Arguments.of(invalidTitleLong), Arguments.of(invalidTitleShort),
                                Arguments.of(invalidDescLong), Arguments.of(invalidDescShort),
                                Arguments.of(invalidPriceNegative));
        }

        @ParameterizedTest
        @MethodSource("prepareInvalidCourseRequestInputs")
        public void courseTeacherController_updateCourse_shouldReturn400IfInvalidInputs(
                        CourseRequest request) throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                request.setId(course.getId());
                var req = createCourseDataPart(request);
                var authToken = jwtUtils.generateToken(course.getTeacher());

                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        // Invalid enums:
        @ParameterizedTest(name = "invalid enum => {0}")
        @CsvSource({ "\"category\": \"INVALID_CATEGORY\"", "\"level\": \"INVALID_LEVEL\"",
                        "\"status\": \"INVALID_STATUS\"" })
        public void courseTeacherController_updateCourse_shouldReturn400IfInvalidEnum(String invalidEnum)
                        throws Exception {

                var course = prepareCourseUtil.prepareCourse();
                String json = "{\"id\": \"" + course.getId() + "\"," + invalidEnum + "}";

                var authToken = jwtUtils.generateToken(course.getTeacher());

                var req = new MockMultipartFile("courseData", null, MediaType.APPLICATION_JSON_VALUE, json.getBytes());
                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        // Validation section ---------

        // Invalid inputs
        // || id not UUID
        @Test
        @DisplayName("Should return 404 if id reefers to nonexisting section")
        public void courseTeacherController_updateCourse_shouldReturn404IfIdRefersToNonexistingSection()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                var authToken = jwtUtils.generateToken(course.getTeacher());
                SectionRequest sRequest = SectionRequest.builder().id(UUID.randomUUID()).build();
                var cRequest = CourseRequest.builder().id(course.getId()).sections(List.of(sRequest)).build();

                var req = createCourseDataPart(cRequest);

                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());

        }

        @Test
        @DisplayName("Should return 400 if section id is not UUID")
        public void courseTeacherController_updateCourse_shouldReturn400IfSectionIdIsNotUUID() throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                String json = "{"
                                + "\"id\": \"" + course.getId() + "\", "
                                + "\"sections\": [ { \"id\": \"INVALID\" } ]"
                                + "}";
                var authToken = jwtUtils.generateToken(course.getTeacher());

                var req = new MockMultipartFile("courseData", null, MediaType.APPLICATION_JSON_VALUE, json.getBytes());
                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        private static Stream<Arguments> prepareInvalidSectionRequestInputs() {
                var valid = SectionRequest.builder().tempId("valid").title("Valid").description("Valid").build();
                var missingTempIdAndId = SectionRequest.builder().build();
                var missingTempId = SectionRequest.builder().title("Valid").description("Valid").build();
                var invalidTitleLong = valid.toBuilder().title("a".repeat(101)).build();
                var invalidTitleShort = valid.toBuilder().title("a").build();
                var invalidDescLong = valid.toBuilder().description("a".repeat(1001)).build();
                var invalidDescShort = valid.toBuilder().description("a").build();

                return Stream.of(Arguments.of(missingTempIdAndId), Arguments.of(missingTempId),
                                Arguments.of(invalidTitleLong), Arguments.of(invalidTitleShort),
                                Arguments.of(invalidDescLong), Arguments.of(invalidDescShort));
        }

        @ParameterizedTest
        @MethodSource("prepareInvalidSectionRequestInputs")
        public void courseTeacherController_updateCourse_shouldReturn400IfSectionRequestInvalid(SectionRequest sR)
                        throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                var cR = CourseRequest.builder().id(course.getId()).sections(List.of(sR)).build();
                var req = createCourseDataPart(cR);
                var authToken = jwtUtils.generateToken(course.getTeacher());

                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        // Validation chapter ----------
        // Invalid inputs
        // || id not UUID
        @Test
        @DisplayName("Should return 400 if chapter id is not UUID")
        public void courseTeacherController_updateCourse_shouldReturn400IfChapterIdIsNotUUID() throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSections(1);
                var section = course.getSections().get(0);
                String json = "{"
                                + "\"id\": \"" + course.getId() + "\", "
                                + "\"sections\": [ { "
                                + "\"id\": \"" + section.getId() + "\", "
                                + "\"chapters\": [ { \"id\": \"INVALID\" } ]"
                                + "} ]"
                                + "}";
                var authToken = jwtUtils.generateToken(course.getTeacher());

                var req = new MockMultipartFile("courseData", null, MediaType.APPLICATION_JSON_VALUE, json.getBytes());
                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        // || chapter id reefers to non existiong chapter
        @Test
        @DisplayName("Should return 404 if chapter id is not UUID")
        public void courseTeacherController_updateCourse_shouldReturn404IfChapterIdRefersToNonExistingChapter()
                        throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSections(1);
                var section = course.getSections().get(0);
                String json = "{"
                                + "\"id\": \"" + course.getId() + "\", "
                                + "\"sections\": [ { "
                                + "\"id\": \"" + section.getId() + "\", "
                                + "\"chapters\": [ { \"id\": \"" + UUID.randomUUID() + "\" } ]"
                                + "} ]"
                                + "}";
                var authToken = jwtUtils.generateToken(course.getTeacher());

                var req = new MockMultipartFile("courseData", null, MediaType.APPLICATION_JSON_VALUE, json.getBytes());
                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        private static Stream<Arguments> prepareInvalidChapterRequestInputs() {
                var valid = ChapterRequest.builder().tempId("valid").title("Valid").content("Valid").build();
                var missingTempIdAndId = ChapterRequest.builder().build();
                var missingTempId = ChapterRequest.builder().title("Valid").content("Valid").build();
                var invalidTitleLong = valid.toBuilder().title("a".repeat(101)).build();

                var invalidTitleShort = valid.toBuilder().title("a").build();
                var invalidContLong = valid.toBuilder().content("a".repeat(1001)).build();
                var invalidContShort = valid.toBuilder().content("a").build();
                var invalidVideoUrl = valid.toBuilder().videoUrl("Not_URL").build();
                return Stream.of(
                                Arguments.of(missingTempIdAndId), Arguments.of(missingTempId),
                                Arguments.of(invalidTitleLong), Arguments.of(invalidTitleShort),
                                Arguments.of(invalidContLong), Arguments.of(invalidContShort),
                                Arguments.of(invalidVideoUrl));
        }

        @ParameterizedTest
        @MethodSource("prepareInvalidChapterRequestInputs")
        public void courseTeacherController_updateCourse_shouldReturn400IfInvalidChapterInputs(ChapterRequest request)
                        throws Exception {
                var course = prepareCourseUtil.prepareCourseWithSections(1);
                var section = course.getSections().get(0);

                var sR = SectionRequest.builder().id(section.getId()).chapters(List.of(request)).build();
                var cR = CourseRequest.builder().id(course.getId()).sections(List.of(sR)).build();
                var authToken = jwtUtils.generateToken(course.getTeacher());

                var req = createCourseDataPart(cR);
                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        // Edge cases ----------
        // empty request body
        @Test
        @DisplayName("Should return 400 if request body is empty")
        public void courseTeacherController_updateCourse_shouldReturn400IfRequestBodyIsEmpty() throws Exception {
                var course = prepareCourseUtil.prepareCourse();
                var authToken = jwtUtils.generateToken(course.getTeacher());

                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", course.getId())
                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        // Non existing course id
        @Test
        @DisplayName("Should return 404 if provided course id does not exist")
        public void courseTeacherController_updateCourse_shouldReturn404IfProvidedCourseIdDoesNotExist()
                        throws Exception {
                UUID courseId = UUID.randomUUID();

                var cR = CourseRequest.builder().id(courseId).build();
                var teacher = prepareTeacherUtil.prepareTeacher();
                var authToken = jwtUtils.generateToken(teacher);

                var req = createCourseDataPart(cR);
                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}", courseId)
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 if mismatch beetwen request and path course id")
        public void courseTeacherController_updateCourse_shouldReturn400IfMistmatchBeetwenRequestAndPathCourseId()
                        throws Exception {
                UUID courseId = UUID.randomUUID();
                var course = prepareCourseUtil.prepareCourse();

                var cR = CourseRequest.builder().id(courseId).build();
                var authToken = jwtUtils.generateToken(course.getTeacher());

                var req = createCourseDataPart(cR);
                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @DisplayName("shouldn't update course status to draft if at least one user enrolled")
        public void courseTeacherController_updateCourse_shouldnt_UpdateCourseStatusToDraftIfAtLeastOneUserEnrolled()
                        throws Exception {
                var student = prepareUserUtil.prepareVerifiedUser();
                var course = prepareCourseWithStudentUtil.prepareCourse(student);

                var cR = CourseRequest.builder().id(course.getId()).status(CourseStatus.DRAFT).build();
                var authToken = jwtUtils.generateToken(course.getTeacher());

                var req = createCourseDataPart(cR);

                mockMvc.perform(MockMvcRequestBuilders
                                .multipart(HttpMethod.PATCH, "/api/v1/teacher/courses/{courseId}",
                                                course.getId())
                                .file(req).cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                var foundCourse = courseRepository.findById(course.getId()).get();
                assertThat(foundCourse.getStatus()).isEqualTo(CourseStatus.PUBLISHED);
        }

}
