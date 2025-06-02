package emil.find_course.user.becomeTeacher.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import emil.find_course.user.becomeTeacher.entity.BecomeTeacher;
import emil.find_course.user.becomeTeacher.enums.BecomeTeacherStatus;
import emil.find_course.user.entity.User;

@Repository
public interface BecomeTeacherRepository extends JpaRepository<BecomeTeacher, UUID> {

    Optional<BecomeTeacher> findByUser(User user);

    Integer countAllBySeenByAdmin(boolean seenByAdmin);

    @Query("SELECT b FROM BecomeTeacher b WHERE"
            + " (COALESCE (:seenByAdmin, NULL) IS NULL OR b.seenByAdmin = :seenByAdmin )"
            + " AND (COALESCE (:status, NULL) IS NULL OR b.status = :status) ")
    Page<BecomeTeacher> searchBecomeTeacherRequest(BecomeTeacherStatus status, Boolean seenByAdmin, Pageable pageable);

}