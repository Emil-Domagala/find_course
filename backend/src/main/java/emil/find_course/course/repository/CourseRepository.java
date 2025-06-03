package emil.find_course.course.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.user.entity.User;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

        // :TODO:Optimize Course lookup. Do not look for course id you later gonna
        // change it to CourseDto. Uneffective. Count enrollments on db querry

        Page<Course> findAllByStatus(CourseStatus status, Pageable pageable);

        Page<Course> findAllByStudents(User students, Pageable pageable);

        @Query("SELECT c FROM Course c WHERE " +
                        "c.status = :status AND (" +
                        "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
                        "(COALESCE(:category, NULL) IS NULL OR c.category = :category))")
        Page<Course> searchCourses(
                        @Param("keyword") String keyword,
                        @Param("status") CourseStatus status,
                        @Param("category") CourseCategory category,
                        Pageable pageable);

        @Query("SELECT c FROM Course c WHERE " +
                        "c.teacher.id = :teacherId AND " +
                        "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR :keyword IS NULL) AND " +
                        "(COALESCE(:category, NULL) IS NULL OR c.category = :category)")
        Page<Course> searchTeacherCourses(
                        @Param("keyword") String keyword,
                        @Param("category") CourseCategory category,
                        @Param("teacherId") UUID teacherId,
                        Pageable pageable);

        Course findByIdAndStatus(UUID id, CourseStatus status);

        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.id = :courseId AND :user MEMBER OF c.students")
        boolean isEnrolled(@Param("courseId") UUID courseId, @Param("user") User user);

}
