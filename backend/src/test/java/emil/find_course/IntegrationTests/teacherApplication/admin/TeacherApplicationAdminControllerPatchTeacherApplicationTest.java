package emil.find_course.IntegrationTests.teacherApplication.admin;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.teacherApplication.PrepareAdminUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherApplicationUtil;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.IntegrationTests.teacherApplication.TeacherApplicationFactory;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.IntegrationTests.user.UserFactory;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.repository.TeacherApplicationRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

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



    // Sucessfully updates TeacherApplications multiple or one
    // - returns noCoontent
    // - changes status
    // - changes seen by admin
    // - TeacherApplicationStatus.ACCEPTED then gives user teacher role

    // Patch when no status update

    // Skips Application if it was already accepted

    // Two updates within this same request last wins

    // if user already has role Teacher it is not being duplicated

    // Returns nothing if TeacherApplicationUpdateRequest send but TeacherApplication not exists

    // Return 403 when NON-ADMIN tries to acces this route

    // Returns 400 when not valid TeacherApplicationUpdateRequest (not UUID, wrong status, seenByAdmin not boolean )

    // Returns 400 when empty list


}
