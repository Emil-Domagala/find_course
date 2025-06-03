package emil.find_course.course.chapter.repository.projection;

import java.util.UUID;

public interface CourseIdChapterIdProjection {
    UUID getCourseId();

    UUID getChapterId();
}
