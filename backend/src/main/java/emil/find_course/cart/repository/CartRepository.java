package emil.find_course.cart.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import emil.find_course.cart.entity.Cart;
import emil.find_course.user.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUser(User user);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.course WHERE c.user = :user")
    Optional<Cart> findByUserWithItemsAndCourses(@Param("user") User user);

}
