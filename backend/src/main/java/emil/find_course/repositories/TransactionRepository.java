package emil.find_course.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import emil.find_course.domains.entities.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}
