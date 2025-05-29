package emil.find_course.services;

import com.stripe.model.PaymentIntent;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.domains.dto.TransactionDto;
import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.Transaction;
import emil.find_course.domains.entities.user.User;

public interface TransactionService {
    public Transaction createTransaction(User user, PaymentIntent paymentIntent, Cart cart);

    public boolean existsByPaymentIntentId(String paymentIntentId);

    public PagingResult<TransactionDto> getTransaction(User user, PaginationRequest request);
}
