package emil.find_course.course.chapter;

import java.util.List;
import java.util.UUID;

import emil.find_course.course.chapter.dto.request.ChapterRequest;
import emil.find_course.course.chapter.entity.Chapter;
import emil.find_course.course.section.entity.Section;
import emil.find_course.user.entity.User;

public interface ChapterService {
    public void syncChapter(Section section, List<ChapterRequest> chapterRequest);

    public Chapter getChapterIfStudentEnrolled(UUID chapterId, User user);
}
