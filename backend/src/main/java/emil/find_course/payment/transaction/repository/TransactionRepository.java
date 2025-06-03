package emil.find_course.payment.transaction.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.payment.transaction.entity.Transaction;
import emil.find_course.user.entity.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    boolean existsByPaymentIntentId(String paymentIntentId);

    Page<Transaction> findAllByUser(User user, Pageable pageable);

}
