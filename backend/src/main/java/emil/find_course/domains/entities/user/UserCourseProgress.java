// package emil.find_course.domains.entities.user;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;

// import emil.find_course.domains.entities.course.Course;
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.PrePersist;
// import jakarta.persistence.PreUpdate;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Getter
// @Setter
// @Entity
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(name = "user_course_progress")
// public class UserCourseProgress {

//     @Id
//     @GeneratedValue(strategy = GenerationType.UUID)
//     private UUID id;

//     private Course course;

//     private User user;

//     @Column(nullable = false)
//     private int overallProgress;

//     private List<UserCourseSectionProgress> sectionsProgress;

//     @Column(nullable = false, updatable = false)
//     private LocalDateTime createdAt;

//     @Column(nullable = false)
//     private LocalDateTime updatedAt;

//     @PrePersist
//     protected void onCreate() {
//         LocalDateTime now = LocalDateTime.now();
//         this.createdAt = now;
//         this.updatedAt = now;
//     }

//     @PreUpdate
//     protected void onUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }

// }
