package emil.find_course.repositories;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.dto.courseProgress.CourseProgressProjection;
import emil.find_course.domains.entities.courseProgress.CourseProgress;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, UUID> {

    Optional<CourseProgressProjection> findProjectedByCourseIdAndUserId(UUID courseId, UUID userId);

    Boolean existsByCourseIdAndUserId(UUID courseId, UUID userId);

    Optional<CourseProgress> findByCourseIdAndUserId(UUID courseId, UUID userId);

    Optional<LocalDateTime> findUpdatedAtByCourseIdAndUserId(UUID courseId, UUID userId);

}
