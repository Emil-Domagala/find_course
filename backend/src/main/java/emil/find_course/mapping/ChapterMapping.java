package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.course.ChapterDto;
import emil.find_course.domains.dto.courseProgress.ChapterStructure;
import emil.find_course.domains.dto.detailsProt.ChapterProtectedDto;
import emil.find_course.domains.entities.course.Chapter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChapterMapping {

    ChapterDto toDto(Chapter chapter);

    ChapterProtectedDto toProtectedDto(Chapter chapter);

    ChapterStructure toChapterStructure(Chapter chapter);

}
