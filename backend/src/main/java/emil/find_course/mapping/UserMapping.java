package emil.find_course.mapping;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.UserDto;
import emil.find_course.domains.entities.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapping {

    public abstract UserDto toDto(User user);

    // TODO: DELETE BELOW AFTER MOVING TO S3
    @AfterMapping
    protected void addFullImageUrl(@MappingTarget UserDto dto, User user) {
        String relativePath = user.getImageUrl();
        if (relativePath != null) {
            String fullUrl = "http://localhost:8080/uploads/images/" + relativePath;
            dto.setImageUrl(fullUrl);
        }
    }
}
