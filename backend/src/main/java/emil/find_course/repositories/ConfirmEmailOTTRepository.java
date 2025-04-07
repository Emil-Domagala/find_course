package emil.find_course.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.ConfirmEmailOTT;
import emil.find_course.domains.entities.user.User;

@Repository
public interface ConfirmEmailOTTRepository extends JpaRepository<ConfirmEmailOTT, UUID> {

    Optional<ConfirmEmailOTT> findByUser(User user);

}
