package emil.find_course.IntegrationTests.course;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import emil.find_course.course.chapter.entity.Chapter;
import emil.find_course.course.entity.Course;
import emil.find_course.course.section.entity.Section;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SectionFactory {

    public static Section createSection(Course course, int position) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Section.builder()
                .course(course)
                .position(position)
                .title(suffix)
                .description(suffix)
                .build();
    }

    public static Section createSection(Course course, int position, List<Chapter> chapters) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Section.builder()
                .course(course)
                .position(position)
                .chapters(chapters)
                .title(suffix)
                .description(suffix)
                .build();
    }

    public static List<Section> createSectionsWithChapters(Course course, int count) {
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Section section = createSection(course, i);
            int numOfChapters = count;
            List<Chapter> chapters = new ArrayList<>();
            for (int j = 0; j < numOfChapters; j++) {
                chapters.add(ChapterFactory.createChapterWithContent(j, section));
            }
            section.setChapters(chapters);
            sections.add(section);
        }
        return sections;
    }

    public static List<Section> createSections(Course course, int count) {
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Section section = createSection(course, i);
            sections.add(section);
        }
        return sections;
    }

}
