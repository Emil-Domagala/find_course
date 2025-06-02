package emil.find_course.payment.transaction;

import com.stripe.model.PaymentIntent;

import emil.find_course.cart.entity.Cart;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.payment.transaction.dto.TransactionDto;
import emil.find_course.payment.transaction.entity.Transaction;
import emil.find_course.user.entity.User;

public interface TransactionService {
    public Transaction createTransaction(User user, PaymentIntent paymentIntent, Cart cart);

    public boolean existsByPaymentIntentId(String paymentIntentId);

    public PagingResult<TransactionDto> getTransaction(User user, PaginationRequest request);
}
