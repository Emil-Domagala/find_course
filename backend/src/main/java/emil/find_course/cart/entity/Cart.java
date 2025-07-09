package emil.find_course.cart.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import emil.find_course.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(name = "cart_cartItem", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "cartItem_id"))
    @Builder.Default
    private Set<CartItem> cartItems = new HashSet<>();

    @Column(nullable = false)
    private Instant expiration;

}
