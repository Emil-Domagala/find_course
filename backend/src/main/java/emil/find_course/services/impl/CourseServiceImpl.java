package emil.find_course.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.enums.CourseStatus;
import emil.find_course.repositories.CourseRepository;
import emil.find_course.services.CourseService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public List<Course> getCourses() {

        return courseRepository.findAllByStatus(CourseStatus.PUBLISHED);
    }

    @Override
    public Course getPublishedCourse(UUID id) {
       return courseRepository.findByIdAndStatus(id, CourseStatus.PUBLISHED);
       
    }

}
