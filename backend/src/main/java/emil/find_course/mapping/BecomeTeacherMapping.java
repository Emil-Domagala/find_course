package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.BecomeTeacherDto;
import emil.find_course.domains.entities.BecomeTeacher;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { UserMapping.class })
public interface BecomeTeacherMapping {

    BecomeTeacherDto toDto(BecomeTeacher cart);
}
