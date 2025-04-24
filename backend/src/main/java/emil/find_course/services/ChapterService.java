package emil.find_course.services;

import java.util.List;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.course.Section;
import emil.find_course.domains.requestDto.course.ChapterRequest;
import emil.find_course.domains.requestDto.course.SectionRequest;

public interface ChapterService {
    public void syncChapter(Section section, List<ChapterRequest> chapterRequest);
}
