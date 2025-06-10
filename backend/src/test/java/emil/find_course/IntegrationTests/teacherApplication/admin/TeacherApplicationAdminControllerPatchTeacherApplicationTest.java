package emil.find_course.IntegrationTests.teacherApplication.admin;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.teacherApplication.PrepareAdminUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherApplicationUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.IntegrationTests.teacherApplication.TeacherApplicationFactory;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.teacherApplication.admin.dto.request.TeacherApplicationUpdateRequest;
import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import emil.find_course.teacherApplication.repository.TeacherApplicationRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;
import emil.find_course.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TeacherApplicationAdminControllerPatchTeacherApplicationTest extends IntegrationTestBase {
        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private PrepareTeacherApplicationUtil prepareTeacherApplicationUtil;
        @Autowired
        private PrepareTeacherUtil prepareTeacherUtil;
        @Autowired
        private PrepareUserUtil prepareUserUtil;
        @Autowired
        private PrepareAdminUtil prepareAdminUtil;

        @Autowired
        private TeacherApplicationRepository teacherApplicationRepository;
        @Autowired
        private UserRepository userRepository;

        User admin;
        User teacher;
        String authToken;

        @BeforeEach
        public void setUp() {
                admin = prepareAdminUtil.prepareUniqueAdmin();
                teacher = prepareTeacherUtil.prepareUniqueTeacher();
                authToken = jwtUtils.generateToken(admin);
        }

        private Map<String, Map<String, Object>> createData(int count, String status) {

                Map<String, Map<String, Object>> data = new HashMap<>();

                while (count > 0) {
                        var user = prepareUserUtil.prepareUniqueVerifiedUse();
                        var savedApp = teacherApplicationRepository
                                        .save(TeacherApplicationFactory.createTeacherApplication(user));
                        var req = TeacherApplicationUpdateRequestFactory.createTeacherApplicationUpdateRequest(
                                        savedApp.getId(), true, TeacherApplicationStatus.valueOf(status));

                        Map<String, Object> innerMap = new HashMap<>();
                        innerMap.put("user", user);
                        innerMap.put("application", savedApp);
                        innerMap.put("request", req);
                        data.put("user-" + count, innerMap);
                        count--;
                }
                return data;
        }

        @Test
        @DisplayName("Should sucessfully update multiple TeacherApplications diferent status")
        public void eacherApplicationAdminController_patchTeacherApplications_shouldSucessfullyUpdateMultipleTeacherApplicationsDiferentStatus()
                        throws Exception {
                var dataP = createData(3, "PENDING");
                var dataA = createData(3, "ACCEPTED");
                var dataD = createData(3, "DENIED");

                ArrayList<UUID> pendingUserIds = new ArrayList<UUID>();
                ArrayList<UUID> acceptedUserIds = new ArrayList<UUID>();
                ArrayList<UUID> deniedUserIds = new ArrayList<UUID>();
                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();

                for (Map.Entry<String, Map<String, Object>> entry : dataP.entrySet()) {
                        updates.add((TeacherApplicationUpdateRequest) entry.getValue().get("request"));
                        pendingUserIds.add(((User) entry.getValue().get("user")).getId());
                }
                for (Map.Entry<String, Map<String, Object>> entry : dataA.entrySet()) {
                        updates.add((TeacherApplicationUpdateRequest) entry.getValue().get("request"));
                        acceptedUserIds.add(((User) entry.getValue().get("user")).getId());
                }
                for (Map.Entry<String, Map<String, Object>> entry : dataD.entrySet()) {
                        updates.add((TeacherApplicationUpdateRequest) entry.getValue().get("request"));
                        deniedUserIds.add(((User) entry.getValue().get("user")).getId());
                }
                String json = objectMapper.writeValueAsString(updates);
                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                // Validate
                for (UUID id : pendingUserIds) {
                        User user = userRepository.findById(id).get();
                        assertThat(user.getRoles()).contains(Role.USER);
                        assertThat(user.getRoles()).doesNotContain(Role.TEACHER);
                        TeacherApplication teacherApplication = teacherApplicationRepository.findByUser(user).get();
                        assertThat(teacherApplication.isSeenByAdmin()).isTrue();
                        assertThat(teacherApplication.getStatus()).isEqualTo(TeacherApplicationStatus.PENDING);
                }
                for (UUID id : deniedUserIds) {
                        User user = userRepository.findById(id).get();
                        assertThat(user.getRoles()).contains(Role.USER);
                        assertThat(user.getRoles()).doesNotContain(Role.TEACHER);
                        TeacherApplication teacherApplication = teacherApplicationRepository.findByUser(user).get();
                        assertThat(teacherApplication.isSeenByAdmin()).isTrue();
                        assertThat(teacherApplication.getStatus()).isEqualTo(TeacherApplicationStatus.DENIED);
                }
                for (UUID id : acceptedUserIds) {
                        User user = userRepository.findById(id).get();
                        assertThat(user.getRoles()).contains(Role.USER);
                        assertThat(user.getRoles()).contains(Role.TEACHER);
                        TeacherApplication teacherApplication = teacherApplicationRepository.findByUser(user).get();
                        assertThat(teacherApplication.isSeenByAdmin()).isTrue();
                        assertThat(teacherApplication.getStatus()).isEqualTo(TeacherApplicationStatus.ACCEPTED);
                }

        }

        @ParameterizedTest(name = "Should sucessfully update multiple TeacherApplications => status: {0}")
        @CsvSource({ "ACCEPTED", "DENIED", "PENDING" })
        public void eacherApplicationAdminController_patchTeacherApplications_shouldSucessfullyUpdateMultipleTeacherApplications(
                        String status)
                        throws Exception {
                var data = createData(3, status);
                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();
                for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                        updates.add((TeacherApplicationUpdateRequest) entry.getValue().get("request"));
                }

                String json = objectMapper.writeValueAsString(updates);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                        User user = (User) entry.getValue().get("user");
                        assertThat(user.getRoles()).contains(Role.USER);
                        TeacherApplication teacherApplication = teacherApplicationRepository.findByUser(user).get();
                        assertThat(teacherApplication.isSeenByAdmin()).isTrue();
                        assertThat(teacherApplication.getStatus()).isEqualTo(TeacherApplicationStatus.valueOf(status));

                        if (status.equals("ACCEPTED")) {
                                assertThat(user.getRoles()).contains(Role.TEACHER);
                        }
                }
        }

        @ParameterizedTest(name = "Should sucessfully update TeacherApplications => status: {0}")
        @CsvSource({ "ACCEPTED", "DENIED", "PENDING" })
        public void eacherApplicationAdminController_patchTeacherApplications_shouldSucessfullyUpdateTeacherApplications(
                        String status)
                        throws Exception {
                var user = prepareUserUtil.prepareUniqueVerifiedUse();
                var app = TeacherApplicationFactory.createTeacherApplication(user);
                var savedApp = teacherApplicationRepository.save(app);
                var req = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(savedApp.getId(), true,
                                                TeacherApplicationStatus.valueOf(status));
                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();
                updates.add(req);
                String json = objectMapper.writeValueAsString(updates);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                TeacherApplication teacherApplication = teacherApplicationRepository.findByUser(user).get();
                assertThat(teacherApplication.isSeenByAdmin()).isTrue();
                assertThat(teacherApplication.getStatus()).isEqualTo(TeacherApplicationStatus.valueOf(status));
                if (status.equals("ACCEPTED")) {
                        assertThat(user.getRoles()).contains(Role.TEACHER);
                }
        }

        @Test
        @DisplayName("Should change seenByAdmin even if only ID passed")
        public void eacherApplicationAdminController_patchTeacherApplications_shouldChangeSeenByAdminEvenIfOnlyIDPassed()
                        throws Exception {
                var user = prepareUserUtil.prepareUniqueVerifiedUse();
                var app = TeacherApplicationFactory.createTeacherApplication(user);
                var savedApp = teacherApplicationRepository.save(app);
                var req = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(savedApp.getId());
                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();
                updates.add(req);
                String json = objectMapper.writeValueAsString(updates);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                assertThat(teacherApplicationRepository.findByUser(user).get().isSeenByAdmin()).isTrue();
        }

        @Test
        @DisplayName("Should not change already accepted application")
        public void teacherApplicationAdminController_patchTeacherApplications_shouldNotChangeAlreadyAcceptedApplication()
                        throws Exception {
                TeacherApplication app = TeacherApplicationFactory.createTeacherApplication(teacher, true,
                                TeacherApplicationStatus.ACCEPTED);
                var savedApp = teacherApplicationRepository.save(app);
                var req = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(savedApp.getId(), true,
                                                TeacherApplicationStatus.DENIED);
                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();
                updates.add(req);
                String json = objectMapper.writeValueAsString(updates);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                assertThat(teacherApplicationRepository.findByUser(teacher).get().getStatus())
                                .isEqualTo(TeacherApplicationStatus.ACCEPTED);
        }

        @Test
        @DisplayName("Should throw 400 when duplicated key")
        public void teacherApplicationAdminController_patchTeacherApplications_shouldThrow400WhenDuplicatedKey()
                        throws Exception {
                var app = prepareTeacherApplicationUtil.praparUniqueTeacherApplication();
                UUID id = app.getId();
                var req1 = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(id);
                var req2 = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(id, false, TeacherApplicationStatus.ACCEPTED);

                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();
                updates.add(req1);
                updates.add(req2);
                String json = objectMapper.writeValueAsString(updates);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        @Test
        @DisplayName("Should not duplicate TeacherRole when user already has it")
        public void teacherApplicationAdminController_patchTeacherApplications_shouldNotDuplicateTeacherRoleWhenUserAlreadyHasIt()
                        throws Exception {
                var application = TeacherApplicationFactory.createTeacherApplication(teacher);
                var savedTeacherApp = teacherApplicationRepository.save(application);

                assertThat(teacherApplicationRepository.count()).isEqualTo(1);
                var request = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(savedTeacherApp.getId());

                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();
                updates.add(request);
                String json = objectMapper.writeValueAsString(updates);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                var changedUser = userRepository.findById(teacher.getId()).orElseThrow();

                assertThat(changedUser.getRoles()).contains(Role.USER);
                assertThat(changedUser.getRoles()).contains(Role.TEACHER);
                assertThat(changedUser.getRoles()).hasSize(2);
        }

        @Test
        @DisplayName("Should return 200 even though TeacherApplication does not exist")
        public void teacherApplicationAdminController_patchTeacherApplications_shouldReturn200WhenTeacherApplicationDoesNotExist()
                        throws Exception {
                var request = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(UUID.randomUUID());
                List<TeacherApplicationUpdateRequest> updates = new ArrayList<>();
                updates.add(request);
                String json = objectMapper.writeValueAsString(updates);

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @ParameterizedTest(name = "Invalid body passed => {0}")
        @CsvSource({ "[]", "[{}]", "[null]", "[{\"id\":null}]", "[{\"id\":\"invalid-uuid-format\"}]" })
        public void teacherApplicationAdminController_patchTeacherApplications_shouldReturn400WhenEmptyListPassed(
                        String json)
                        throws Exception {
                prepareTeacherApplicationUtil.praparUniqueTeacherApplication();

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .content(json)
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when no body")
        public void teacherApplicationAdminController_patchTeacherApplications_shouldReturn400WhenNoBodyPassed()
                        throws Exception {
                prepareTeacherApplicationUtil.praparUniqueTeacherApplication();

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 403 when user is not admin")
        public void teacherApplicationAdminController_patchTeacherApplications_shouldReturn403WhenUserIsNotAdmin()
                        throws Exception {
                User user = prepareUserUtil.prepareUniqueVerifiedUse();
                var authToken = jwtUtils.generateToken(user);

                var application = prepareTeacherApplicationUtil.praparTeacherApplication(user);
                var update = TeacherApplicationUpdateRequestFactory
                                .createTeacherApplicationUpdateRequest(application.getId());
                String json = objectMapper.writeValueAsString(List.of(update));

                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/admin/teacher-application")
                                .cookie(new Cookie(authCookieName, authToken))
                                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                                .content(json))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());

        }

}
