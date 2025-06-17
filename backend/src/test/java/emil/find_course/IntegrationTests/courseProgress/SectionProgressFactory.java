package emil.find_course.IntegrationTests.courseProgress;

import java.util.ArrayList;
import java.util.List;

import emil.find_course.course.section.entity.Section;
import emil.find_course.courseProgress.entity.CourseProgress;
import emil.find_course.courseProgress.entity.SectionProgress;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SectionProgressFactory {

    public static List<SectionProgress> createSectionProgress(List<Section> sections,
            CourseProgress courseProgress) {
        List<SectionProgress> sectionsProgress = new ArrayList<>();
        for (Section sec : sections) {
            var sectionProgress = SectionProgress.builder()
                    .originalSection(sec)
                    .courseProgress(courseProgress)
                    .position(sec.getPosition())
                    .build();
            sectionProgress
                    .setChapters(ChapterProgressFactory.createChapterProgresses(sec.getChapters(), sectionProgress));

            sectionsProgress.add(sectionProgress);
        }
        return sectionsProgress;
    }

}
