package emil.find_course.services;

import java.util.List;
import java.util.UUID;

import emil.find_course.domains.entities.course.Course;

public interface CourseService {

    List<Course> getCourses();

    Course getPublishedCourse(UUID id);

}
