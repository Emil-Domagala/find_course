package emil.find_course.IntegrationTests.course;

import java.util.UUID;

import emil.find_course.course.chapter.entity.Chapter;
import emil.find_course.course.section.entity.Section;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ChapterFactory {

    public static Chapter createChapterWithContent(int position, Section section) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Chapter.builder()
                .section(section)
                .position(position)
                .title(suffix)
                .content(suffix)
                .build();
    }

    public static Chapter createChapterWithVideo(int position, Section section) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Chapter.builder()
                .section(section)
                .position(position)
                .title(suffix)
                .videoUrl("https://placehold.co/600x400")
                .build();
    }

    public static Chapter createChapterWithVideoAndContent(int position, Section section) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Chapter.builder()
                .section(section)
                .position(position)
                .title(suffix)
                .content(suffix)
                .videoUrl("https://placehold.co/600x400")
                .build();
    }

}
