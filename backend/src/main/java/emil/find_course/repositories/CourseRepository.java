package emil.find_course.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.enums.CourseStatus;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    Page<Course> findAllByStatus(CourseStatus status, Pageable pageable);

    Page<Course> findAllByStudents(User students, Pageable pageable);

    Course findByIdAndStatus(UUID id, CourseStatus status);

}
