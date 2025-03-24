// package emil.find_course.domains.entities.user;

// import java.util.UUID;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
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
// @Table(name = "user_course_chapter_progress")
// public class UserCourseChapterProgress {

//     @Id
//     @GeneratedValue(strategy = GenerationType.UUID)
//     private UUID id;

//     @Column(nullable = false)
//     @Builder.Default
//     private boolean completed = false;

//     @Column(nullable = false)
//     private int lastPosition;

// }
