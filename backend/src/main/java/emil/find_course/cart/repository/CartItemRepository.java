package emil.find_course.cart.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.course.entity.Course;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM CartItem ci WHERE ci.course = :course AND ci.cart = :cart")
    boolean isCourseInCart(@Param("course") Course course, @Param("cart") Cart cart);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.id = :userId AND ci.course.id = :courseId")
    Optional<CartItem> findByUserAndCourseId(UUID userId, UUID courseId);
}
