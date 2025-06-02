package emil.find_course.courseProgress.repository.projection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import emil.find_course.course.chapter.enums.ChapterType;

public interface CourseProgressProjection {
    UUID getId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    CourseInfo getCourse();

    List<SectionProgressView> getSections();

    interface CourseInfo {
        UUID getId();

        String getTitle();
    }

    interface SectionProgressView {
        UUID getId();

        SectionInfo getOriginalSection();

        List<ChapterProgressView> getChapters();
    }

    interface SectionInfo {
        UUID getId();

        String getTitle();
    }

    interface ChapterProgressView {
        UUID getId();

        boolean isCompleted();

        // int getLastPosition();

        ChapterInfo getOriginalChapter();
    }

    interface ChapterInfo {
        UUID getId();

        String getTitle();

        ChapterType getType();
    }
}