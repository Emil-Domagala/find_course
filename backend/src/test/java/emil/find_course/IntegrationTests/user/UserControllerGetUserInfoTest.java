package emil.find_course.IntegrationTests.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import emil.find_course.IntegrationTests.IntegrationTestBase;


@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerGetUserInfoTest extends IntegrationTestBase {
    
}
