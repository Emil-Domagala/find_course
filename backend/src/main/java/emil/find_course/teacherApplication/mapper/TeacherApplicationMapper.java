package emil.find_course.teacherApplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.teacherApplication.dto.TeacherApplicationDto;
import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.user.mapper.UserMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { UserMapper.class })
public interface TeacherApplicationMapper {

    TeacherApplicationDto toDto(TeacherApplication cart);
}
