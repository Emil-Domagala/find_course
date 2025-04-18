package emil.find_course.services;

import com.stripe.model.PaymentIntent;

import emil.find_course.domains.dto.TransactionDto;
import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.Transaction;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;

public interface TransactionService {
    public Transaction createTransaction(User user, PaymentIntent paymentIntent, Cart cart);

    public boolean existsByPaymentIntentId(String paymentIntentId);

    public PagingResult<TransactionDto> getTransaction(User user, PaginationRequest request);
}
