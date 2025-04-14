package emil.find_course.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.user.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUser(User user);

}
