package emil.find_course.IntegrationTests.courseProgress;

import java.util.ArrayList;
import java.util.List;

import emil.find_course.course.chapter.entity.Chapter;
import emil.find_course.courseProgress.entity.ChapterProgress;
import emil.find_course.courseProgress.entity.SectionProgress;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ChapterProgressFactory {
    public static List<ChapterProgress> createChapterProgresses(List<Chapter> chapters,
            SectionProgress sectionProgress) {
        List<ChapterProgress> chaptersProgress = new ArrayList<>();
        for (Chapter ch : chapters) {
            chaptersProgress.add(
                    ChapterProgress.builder()
                            .originalChapter(ch)
                            .sectionProgress(sectionProgress)
                            .position(ch.getPosition())
                            .build());
        }
        return chaptersProgress;
    }
}
