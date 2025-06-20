package emil.find_course.user.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import emil.find_course.course.entity.Course;
import emil.find_course.user.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String userLastname;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>(List.of(Role.USER));

    @Column(nullable = false)
    private String password;

    @ManyToMany(mappedBy = "students")
    @Builder.Default
    private Set<Course> enrollmentCourses = new HashSet<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Course> teachingCourses = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean isEmailVerified = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public void teachCourse(Course course) {
        if (!roles.contains(Role.TEACHER)) {
            throw new IllegalStateException("Only teachers can teach courses.");
        }
        teachingCourses.add(course);

    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", username=" + username + ", userLastname=" + userLastname
                + ", roles=" + roles + ", password=" + password + ", isEmailVerified=" + isEmailVerified
                + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
