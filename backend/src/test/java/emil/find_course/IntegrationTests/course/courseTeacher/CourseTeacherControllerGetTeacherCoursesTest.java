package emil.find_course.IntegrationTests.course.courseTeacher;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CourseTeacherControllerGetTeacherCoursesTest extends IntegrationTestBase{
    



    // Should successfully return one course
    // Should successfully return multiple courses
    // Should return empty content if not owner
    // Should return successfully course without chapter
    // Pagination works (parameterized test)
    // Should return to default page, size, sortField if passed incorrect values
    // Should return to default page, size, sortField if not passed
    //  Valid sortField works (parameterized test)
    // Direction works (parameterized test)
    // Sorting by title alphabetically in ascending order
    // Fallback and default values + sorting behavior
    
}
