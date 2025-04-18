package emil.find_course.repositories;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.Transaction;
import emil.find_course.domains.entities.user.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    boolean existsByPaymentIntentId(String paymentIntentId);

    Page<Transaction> findAllByUser(User user, Pageable pageable);

}
