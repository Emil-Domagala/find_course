package emil.find_course.user.becomeTeacher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.user.becomeTeacher.dto.BecomeTeacherDto;
import emil.find_course.user.becomeTeacher.entity.BecomeTeacher;
import emil.find_course.user.mapper.UserMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { UserMapper.class })
public interface BecomeTeacherMapper {

    BecomeTeacherDto toDto(BecomeTeacher cart);
}
