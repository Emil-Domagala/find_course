package emil.find_course.courseProgress.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import emil.find_course.courseProgress.entity.CourseProgress;
import emil.find_course.courseProgress.repository.projection.CourseProgressProjection;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, UUID> {

    Optional<CourseProgressProjection> findProjectedByCourseIdAndUserId(UUID courseId, UUID userId);

    Boolean existsByCourseIdAndUserId(UUID courseId, UUID userId);

    Optional<CourseProgress> findByCourseIdAndUserId(UUID courseId, UUID userId);

@Query("SELECT cp.updatedAt FROM CourseProgress cp WHERE cp.course.id = :courseId AND cp.user.id = :userId")
    Optional<LocalDateTime> findUpdatedAtByCourseIdAndUserId(
            @Param("courseId") UUID courseId,
            @Param("userId") UUID userId);

}
