package emil.find_course.course;

import java.util.UUID;

import emil.find_course.course.entity.Course;

public interface CourseService {

    // common
    public Course getById(UUID id);
}
