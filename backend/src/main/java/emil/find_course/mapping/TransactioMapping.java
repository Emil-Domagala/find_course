package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.TransactionDto;
import emil.find_course.domains.entities.Transaction;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { UserMapping.class })
public interface TransactioMapping {
    TransactionDto toDto(Transaction transaction);
}
