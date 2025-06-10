package emil.find_course.IntegrationTests.teacherApplication.admin;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.teacherApplication.PrepareAdminUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherApplicationUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.teacherApplication.dto.TeacherApplicationDto;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TeacherApplicationAdminControllerGetTeacherApplicationsTest extends IntegrationTestBase {
    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrepareAdminUtil prepareAdminUtil;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private PrepareTeacherApplicationUtil prepareTeacherApplicationUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private List<TeacherApplicationDto> extractContent(MvcResult res) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PagingResult.class,
                TeacherApplicationDto.class);

        PagingResult<TeacherApplicationDto> result = objectMapper.readValue(res.getResponse().getContentAsString(),
                type);

        return result.getContent().stream().toList();
    }

    @Test
    @DisplayName("Should return top 100 elements")
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnTop100Elements() throws Exception {
        int size = 150;
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(150);
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("size", String.valueOf(size))
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(size))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value((int) Math.ceil(size / 100) + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(100));

    }

    @Test
    @DisplayName("Should return default params and be sorted by createdAt ASCENDING")
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnDefaultParams() throws Exception {
        int defSize = PaginationRequest.DEFAULT_SIZE;
        int count = 30;
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(count / 6);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(count / 6);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(count / 6);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(count / 6);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(count / 6);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(count / 6);
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(count))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value((int) Math.ceil(count / defSize) + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(defSize))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(defSize)).andReturn();

        List<TeacherApplicationDto> content = extractContent(res);

        for (int i = 0; i < content.size() - 1; i++) {
            LocalDateTime current = content.get(i).getCreatedAt();
            LocalDateTime next = content.get(i + 1).getCreatedAt();
            assertTrue(current.isBefore(next));
        }

    }

    // Pagination works (list size, total elements, curr page)
    @ParameterizedTest
    @CsvSource({
            "5,10,0",
            "10,10,0",
            "5,10,1",
    })
    public void teacherApplicationAdminControlle_getTeacherApplications_paginationWorks(String size, String totEle,
            String currPage) throws Exception {
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(Integer.parseInt(totEle));
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("page", currPage)
                .param("size", size)
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(totEle))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(Integer.parseInt(currPage) + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(size))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size)).andReturn();

    }

    @ParameterizedTest(name = "Filter by seenByAdmin => seenByAdmin {0}")
    @CsvSource({ "true", "false" })
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnJustCorrectSeenByAdmin(
            Boolean seenByAdmin) throws Exception {
        int totalElem = 20;
        int halfElem = totalElem / 2;
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(halfElem, true);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(halfElem, false);
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("size", Integer.toString(totalElem))
                .param("seenByAdmin", Boolean.toString(seenByAdmin))
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(totalElem))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(halfElem))
                .andReturn();

        List<TeacherApplicationDto> content = extractContent(res);

        for (int i = 0; i < content.size() - 1; i++) {
            boolean current = content.get(i).isSeenByAdmin();
            assertTrue(current == seenByAdmin);
        }

    }

    // Filtering by status works
    @ParameterizedTest(name = "Filter by TeacherApplicationStatus => TeacherApplicationStatus {0}")
    @CsvSource({ "PENDING", "ACCEPTED", "DENIED" })
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnJustCorrectStatus(
            String status) throws Exception {
        int totalElem = 30;
        int thirdEl = totalElem / 3;
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(thirdEl, false,
                TeacherApplicationStatus.PENDING);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(thirdEl, true,
                TeacherApplicationStatus.ACCEPTED);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(thirdEl, true,
                TeacherApplicationStatus.DENIED);
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("size", Integer.toString(totalElem))
                .param("status", status)
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(totalElem))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(thirdEl))
                .andReturn();

        List<TeacherApplicationDto> content = extractContent(res);

        TeacherApplicationStatus expStatus = TeacherApplicationStatus.valueOf(status);
        for (int i = 0; i < content.size() - 1; i++) {
            TeacherApplicationStatus current = content.get(i).getStatus();
            assertTrue(current.equals(expStatus));
        }

    }

    // sort id,
    @ParameterizedTest
    @CsvSource({
            "ASC", "DESC"
    })
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnAllEntitiesButSortedByID(
            String direction) throws Exception {
        int totalElem = 20;
        int halfElem = totalElem / 2;
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(halfElem);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(halfElem);
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("size", Integer.toString(totalElem))
                .param("sortField", "id")
                .param("direction", direction)
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(totalElem))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(totalElem))
                .andReturn();

        List<TeacherApplicationDto> content = extractContent(res);

        for (int i = 0; i < content.size() - 1; i++) {
            String currentIdStr = content.get(i).getId().toString();
            String nextIdStr = content.get(i + 1).getId().toString();

            if ("ASC".equals(direction)) {
                assertThat(currentIdStr).isLessThan(nextIdStr);
            } else {
                assertThat(currentIdStr).isGreaterThan(nextIdStr);
            }
        }
    }

    // sort seenByAdmin
    @ParameterizedTest
    @CsvSource({
            "ASC", "DESC"
    })
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnAllEntitiesButSortedByseenByAdmin(
            String direction) throws Exception {
        int totalElem = 20;
        int halfElem = totalElem / 2;
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(halfElem, true);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(halfElem, false);
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("size", Integer.toString(totalElem))
                .param("sortField", "seenByAdmin")
                .param("direction", direction)
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(totalElem))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(totalElem))
                .andReturn();

        List<TeacherApplicationDto> content = extractContent(res);

        boolean expectedFirstVal = "ASC".equals(direction);

        int start = 0;
        int end = totalElem - 1;
        while (start < end) {
            boolean beg = content.get(start).isSeenByAdmin();
            boolean fin = content.get(end).isSeenByAdmin();
            assertThat(beg).isEqualTo(!expectedFirstVal);
            assertThat(fin).isEqualTo(expectedFirstVal);
            start++;
            end--;
        }
    }

    // sort by status works
    @ParameterizedTest
    @CsvSource({
            "ASC", "DESC"
    })
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnAllEntitiesButSortedByStatus(
            String direction) throws Exception {
        int totalElem = 30;
        int thirdEl = totalElem / 3;
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(thirdEl, false,
                TeacherApplicationStatus.PENDING);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(thirdEl, true,
                TeacherApplicationStatus.ACCEPTED);
        prepareTeacherApplicationUtil.createAndPersistUniqueUserAndTeacherApplications(thirdEl, true,
                TeacherApplicationStatus.DENIED);
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("size", Integer.toString(totalElem))
                .param("direction", direction)
                .param("sortField", "status")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(totalElem))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(totalElem))
                .andReturn();

        List<TeacherApplicationDto> content = extractContent(res);

        for (int i = 0; i < content.size() - 1; i++) {
            TeacherApplicationStatus current = content.get(i).getStatus();
            TeacherApplicationStatus next = content.get(i + 1).getStatus();
            if ("ASC".equals(direction)) {
                assertThat(current.name()).isLessThanOrEqualTo(next.name());
            } else {
                assertThat(current.name()).isGreaterThanOrEqualTo(next.name());
            }
        }
    }

    @Test
    @DisplayName("Should return empty TeacherApplicationDto if db empty")
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturnEmptyTeacherApplicationDtoIfDbEmpty()
            throws Exception {
        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.empty").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));

    }

    @Test
    @DisplayName("Should Return 200 For Invalid types that gets to default")
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturn200ForInvalidInputsThatGetsToDefault()
            throws Exception {

        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("page", "-1")
                .param("size", "-5")
                .param("sortField", "unknownField")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(
                        PaginationRequest.DEFAULT_PAGE + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(
                        PaginationRequest.DEFAULT_SIZE))
                .andReturn();

        List<TeacherApplicationDto> content = extractContent(res);

        for (int i = 0; i < content.size() - 1; i++) {
            LocalDateTime current = content.get(i).getCreatedAt();
            LocalDateTime next = content.get(i + 1).getCreatedAt();
            assertTrue(current.isBefore(next));
        }

    }

    @ParameterizedTest(name = "Invalid inputs => direction: {0}, status: {1}, seenByAdmin: {2}")
    @CsvSource({
            "INVALID,PENDING,false",
            "ASC,INVALID,false",
            "ASC,PENDING,INVALID"
    })
    public void teacherApplicationAdminControlle_getTeacherApplications_shouldReturn400ForInvalidInputs(
            String direction,
            String status,
            String seenByAdmin) throws Exception {

        var admin = prepareAdminUtil.prepareAdmin();
        var authToken = jwtUtils.generateToken(admin);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .param("direction", direction)
                .param("status", status)
                .param("seenByAdmin", seenByAdmin)
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 403 when user is not admin")
    public void teacherApplicationAdminController_getTeacherApplications_shouldReturn403WhenUserIsNotAdmin()
            throws Exception {
        var user = prepareUserUtil.prepareVerifiedUser();
        var authToken = jwtUtils.generateToken(user);

        prepareTeacherApplicationUtil.praparTeacherApplication(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/teacher-application")
                .cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

}
