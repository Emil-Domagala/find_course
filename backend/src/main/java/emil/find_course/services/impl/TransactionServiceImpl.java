package emil.find_course.services.impl;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.stripe.model.PaymentIntent;

import emil.find_course.domains.dto.TransactionDto;
import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.Transaction;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.mapping.TransactioMapping;
import emil.find_course.repositories.TransactionRepository;
import emil.find_course.services.TransactionService;
import emil.find_course.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactioMapping transactionMapping;

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
        final List<TransactionDto> transactionsDto = transactions.stream().map(transactionMapping::toDto).toList();

        System.out.println(request.toString());

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
