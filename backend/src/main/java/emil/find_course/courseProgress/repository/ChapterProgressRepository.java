package emil.find_course.courseProgress.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import emil.find_course.courseProgress.entity.ChapterProgress;

@Repository
public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, UUID> {

        @Query("SELECT cp FROM ChapterProgress cp " +
                        "JOIN cp.sectionProgress sp " +
                        "JOIN sp.courseProgress cpr " +
                        "WHERE cpr.user.id = :userId " +
                        "AND cpr.course.id = :courseId " +
                        "AND cp.id = :chapterProgresId")
        Optional<ChapterProgress> findByUserCourseAndOriginalChapter(
                        @Param("userId") UUID userId,
                        @Param("courseId") UUID courseId,
                        @Param("chapterProgresId") UUID chapterProgresId);

}
