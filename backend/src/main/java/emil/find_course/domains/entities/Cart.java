package emil.find_course.domains.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
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
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cart_course", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn)
    @Builder.Default
    private HashSet<Course> courses = new HashSet<>();

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private Instant expiration;

}
