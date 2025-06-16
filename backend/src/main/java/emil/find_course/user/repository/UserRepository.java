package emil.find_course.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import emil.find_course.course.entity.Course;
import emil.find_course.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c = :course AND :user MEMBER OF c.students")
    boolean isUserEnrolledInCourse(User user, Course course);
    

}
