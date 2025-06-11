package emil.find_course.IntegrationTests.course.coursePublic;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CoursePublicControllerGetCourseTest extends IntegrationTestBase {
    
}
