package emil.find_course.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.BecomeTeacher;
import emil.find_course.domains.entities.user.User;

@Repository
public interface BecomeTeacherRepository extends JpaRepository<BecomeTeacher, UUID> {

    Optional<BecomeTeacher> findByUser(User user);

    Integer countAllBySeenByAdmin(boolean seenByAdmin);

    @Query("SELECT b FROM BecomeTeacher b WHERE (COALESCE (:seenByAdmin, NULL) IS NULL OR b.seenByAdmin = :seenByAdmin )")
    Page<BecomeTeacher> searchBecomeTeacherRequest(boolean seenByAdmin, Pageable pageable);

}