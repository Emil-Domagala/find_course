package emil.find_course.course.chapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.course.chapter.dto.ChapterDto;
import emil.find_course.course.chapter.dto.prot.ChapterProtectedDto;
import emil.find_course.course.chapter.entity.Chapter;
import emil.find_course.courseProgress.dto.ChapterStructure;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChapterMapper {

    ChapterDto toDto(Chapter chapter);

    ChapterProtectedDto toProtectedDto(Chapter chapter);

    ChapterStructure toChapterStructure(Chapter chapter);

}
