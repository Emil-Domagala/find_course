package emil.find_course.course;

import java.util.UUID;

import org.springframework.stereotype.Service;

import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
   
    @Override
    public Course getById(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course not found"));
    }

}
