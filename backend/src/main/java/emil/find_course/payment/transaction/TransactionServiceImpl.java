package emil.find_course.payment.transaction;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.stripe.model.PaymentIntent;

import emil.find_course.cart.entity.Cart;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PaginationUtils;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.payment.transaction.dto.TransactionDto;
import emil.find_course.payment.transaction.entity.Transaction;
import emil.find_course.payment.transaction.mapper.TransactionMapper;
import emil.find_course.payment.transaction.repository.TransactionRepository;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction createTransaction(User user, PaymentIntent paymentIntent, Cart cart) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .amount((int) (paymentIntent.getAmount() / 1))
                .paymentIntentId(paymentIntent.getId())
                .courses(Set.copyOf(cart.getCourses()))
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public PagingResult<TransactionDto> getTransaction(User user, PaginationRequest request) {
        final Pageable pageable = PaginationUtils.getPageable(request);

        final Page<Transaction> transactions = transactionRepository.findAllByUser(user, pageable);
        final List<TransactionDto> transactionsDto = transactions.stream().map(transactionMapper::toDto).toList();

        return new PagingResult<TransactionDto>(
                transactionsDto,
                transactions.getTotalPages(),
                transactions.getTotalElements(),
                transactions.getSize(),
                transactions.getNumber(),
                transactions.isEmpty());

    }

    @Override
    public boolean existsByPaymentIntentId(String paymentIntentId) {
        return transactionRepository.existsByPaymentIntentId(paymentIntentId);
    }

}
