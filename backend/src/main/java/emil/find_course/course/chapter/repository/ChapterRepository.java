package emil.find_course.course.chapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import emil.find_course.course.chapter.entity.Chapter;
import emil.find_course.course.chapter.repository.projection.CourseIdChapterIdProjection;
import emil.find_course.user.entity.User;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {

        @Query("SELECT c.id as courseId, ch.id as chapterId FROM Chapter ch " +
                        "JOIN ch.section s " +
                        "JOIN s.course c " +
                        "WHERE c.id IN :courseIds AND s.position = 0 AND ch.position = 0")
        List<CourseIdChapterIdProjection> findFirstChapterIdsForCourses(@Param("courseIds") List<UUID> courseIds);

        @Query("SELECT ch FROM Chapter ch " +
                        "JOIN ch.section s " +
                        "JOIN s.course c " +
                        "WHERE ch.id = :chapterId AND :user MEMBER OF c.students AND c.id = :courseId")
        Optional<Chapter> findChapterByIdIfUserEnrolled(UUID courseId,UUID chapterId, User user);

}
