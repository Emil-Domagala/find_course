package emil.find_course.course.section.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.course.section.dto.SectionDto;
import emil.find_course.course.section.dto.prot.SectionProtectedDto;
import emil.find_course.course.section.dto.pub.SectionPublicDto;
import emil.find_course.course.section.entity.Section;
import emil.find_course.courseProgress.dto.SectionStructure;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectionMapping {

    SectionDto toDto(Section section);

    SectionProtectedDto toProtectedDto(Section section);

    SectionPublicDto toPublicDto(Section section);

    SectionStructure toSectionStructure(Section section);

}
