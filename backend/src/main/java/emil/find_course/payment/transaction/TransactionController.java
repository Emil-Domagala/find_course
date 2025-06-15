package emil.find_course.payment.transaction;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.payment.transaction.dto.TransactionDto;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("transaction")
    public ResponseEntity<PagingResult<TransactionDto>> getTransactions(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (size > 100) {
            size = 100;
        }
        final User user = userDetails.getUser();

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<TransactionDto> transactions = transactionService.getTransaction(user, request);

        return ResponseEntity.ok(transactions);
    }

}