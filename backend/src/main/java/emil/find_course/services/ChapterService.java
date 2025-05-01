package emil.find_course.services;

import java.util.List;
import java.util.UUID;

import emil.find_course.domains.entities.course.Chapter;
import emil.find_course.domains.entities.course.Section;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.course.ChapterRequest;

public interface ChapterService {
    public void syncChapter(Section section, List<ChapterRequest> chapterRequest);

    public Chapter getChapterIfStudentEnrolled(UUID chapterId, User user);
}
