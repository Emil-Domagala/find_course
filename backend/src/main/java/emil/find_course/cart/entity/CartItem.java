package emil.find_course.cart.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import emil.find_course.course.entity.Course;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cart cart;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Course course;

    @Check(constraints = "price_at_addition >= 0")
    @Column(nullable = false)
    private int priceAtAddition;

    @Column(nullable = false)
    private Instant addedAt;

    @PrePersist
    protected void onSave() {
        this.addedAt = Instant.now();
        this.priceAtAddition = this.course.getPrice();
    }

}
