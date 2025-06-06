package emil.find_course.teacherApplication.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import emil.find_course.user.entity.User;

@Repository
public interface TeacherApplicationRepository extends JpaRepository<TeacherApplication, UUID> {

    Optional<TeacherApplication> findByUser(User user);

    Integer countAllBySeenByAdmin(boolean seenByAdmin);

    @Query("SELECT b FROM TeacherApplication b WHERE"
            + " (COALESCE (:seenByAdmin, NULL) IS NULL OR b.seenByAdmin = :seenByAdmin )"
            + " AND (COALESCE (:status, NULL) IS NULL OR b.status = :status) ")
    Page<TeacherApplication> searchTeacherApplication(TeacherApplicationStatus status, Boolean seenByAdmin,
            Pageable pageable);

}