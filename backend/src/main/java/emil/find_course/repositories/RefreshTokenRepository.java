package emil.find_course.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import emil.find_course.domains.entities.ConfirmEmailOTT;
import emil.find_course.domains.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<ConfirmEmailOTT> findByToken(String token);
}
