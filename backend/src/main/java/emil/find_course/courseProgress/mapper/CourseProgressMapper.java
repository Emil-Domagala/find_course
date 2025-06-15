package emil.find_course.courseProgress.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import emil.find_course.course.chapter.mapper.ChapterMapper;
import emil.find_course.course.mapper.CourseMapper;
import emil.find_course.course.section.mapper.SectionMapper;
import emil.find_course.courseProgress.dto.CourseProgressDto;
import emil.find_course.courseProgress.entity.ChapterProgress;
import emil.find_course.courseProgress.entity.CourseProgress;
import emil.find_course.courseProgress.entity.SectionProgress;
import emil.find_course.courseProgress.repository.projection.CourseProgressProjection;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { CourseMapper.class,
        SectionMapper.class,
        ChapterMapper.class })
public interface CourseProgressMapper {

    @Mapping(target = "overallProgress", source = "sections", qualifiedByName = "calculateOverallProgress")
    CourseProgressDto toDto(CourseProgress courseProgress);

    @Mapping(target = "overallProgress", source = "sections", qualifiedByName = "calculateOverallProgress")
    CourseProgressDto toDto(CourseProgressProjection courseProgressProjection);

    @Named("calculateOverallProgress")
    default int calculateOverallProgress(List<SectionProgress> sections) {
        if (sections == null || sections.isEmpty()) {
            return 0;
        }
        int totalCompletedChapters = 0;
        int totalChapters = 0;
        for (SectionProgress sectionProgress : sections) {
            List<ChapterProgress> chapters = sectionProgress.getChapters();
            if (chapters != null) {
                for (ChapterProgress chapterProgress : chapters) {
                    totalChapters++;
                    if (chapterProgress.isCompleted()) {
                        totalCompletedChapters++;
                    }
                }
            }
        }
        if (totalChapters == 0) {
            return 0;
        }
        return (int) Math.round(((double) totalCompletedChapters * 100) / totalChapters);
    }

    @Named("calculateOverallProgress")
    default int calculateOverallProgressFromProjection(List<CourseProgressProjection.SectionProgressView> sections) {
        if (sections == null || sections.isEmpty()) {
            return 0;
        }
        int totalCompletedChapters = 0;
        int totalChapters = 0;
        for (CourseProgressProjection.SectionProgressView sectionProgress : sections) {
            List<CourseProgressProjection.ChapterProgressView> chapterViews = sectionProgress.getChapters();
            if (chapterViews != null) {
                for (CourseProgressProjection.ChapterProgressView chapterView : chapterViews) {
                    totalChapters++;
                    if (chapterView.isCompleted()) {
                        totalCompletedChapters++;
                    }
                }
            }

        }
        if (totalChapters == 0) {
            return 0;
        }
        return (int) Math.round(((double) totalCompletedChapters * 100) / totalChapters);
    }

}
