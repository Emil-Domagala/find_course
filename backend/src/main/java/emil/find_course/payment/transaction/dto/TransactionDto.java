package emil.find_course.payment.transaction.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import emil.find_course.course.dto.CourseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private UUID id;
    private String paymentIntentId;
    private int amount;
    private Instant createdAt;
    private Set<CourseDto> courses;

    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "amount", "id");
}
