package emil.find_course.payment.transaction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.payment.transaction.dto.TransactionDto;
import emil.find_course.payment.transaction.entity.Transaction;
import emil.find_course.user.mapper.UserMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { UserMapper.class })
public interface TransactioMapping {
    TransactionDto toDto(Transaction transaction);
}
