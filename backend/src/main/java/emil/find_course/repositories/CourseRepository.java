package emil.find_course.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.enums.CourseStatus;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findAllByStatus(CourseStatus status);
}
