package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.CartDto;
import emil.find_course.domains.entities.Cart;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,uses={CourseMapping.class})
public interface CartMapping {

    CartDto toDto(Cart cart);
}
