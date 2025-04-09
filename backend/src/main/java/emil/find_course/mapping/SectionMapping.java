package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.SectionDto;
// import emil.find_course.domains.dto.detailsProt.SectionProtectedDto;
// import emil.find_course.domains.dto.detailsPub.SectionPublicDto;
import emil.find_course.domains.entities.course.Section;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectionMapping {

    SectionDto toDto(Section section);

    // SectionProtectedDto toProtectedDto(Section section);

    // SectionPublicDto toPublicDto(Section section);

}
