package emil.find_course.services;

import java.util.List;
import java.util.UUID;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.RequestCourseBody;

public interface CourseService {

    // Public
    Course getPublishedCourse(UUID id);

    List<Course> getPublishedCourses();


// Teacher
    
    Course createCourse(RequestCourseBody requestCourseBody, User teacher);
    
    Course updateCourse(RequestCourseBody requestCourseBody, User teacher);
    
    UUID deleteCourse(UUID id, UUID teacherId);
    
    
    //Student 
    List<Course> getUserEnrolledCourses(User student);
}
