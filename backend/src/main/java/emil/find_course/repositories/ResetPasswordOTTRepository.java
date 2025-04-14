package emil.find_course.repositories;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.ResetPasswordOTT;
import emil.find_course.domains.entities.user.User;

@Repository
public interface ResetPasswordOTTRepository extends JpaRepository<ResetPasswordOTT, UUID> {

    Optional<ResetPasswordOTT> findByUser(User user);

    Optional<ResetPasswordOTT> findByToken(String token);

    @Modifying
    @Query("DELETE FROM ResetPasswordOTT c WHERE c.expiration < ?1")
    int deleteByExpirationBefore(Instant now);

    @Modifying
    void deleteByUser(User user);

}
