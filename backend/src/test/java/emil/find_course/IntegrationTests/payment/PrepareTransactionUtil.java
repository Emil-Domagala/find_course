package emil.find_course.IntegrationTests.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.course.entity.Course;
import emil.find_course.payment.transaction.entity.Transaction;
import emil.find_course.payment.transaction.repository.TransactionRepository;
import emil.find_course.user.entity.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PrepareTransactionUtil {

    private TransactionRepository transactionRepository;
    private PrepareCourseWithStudentUtil prepareCourseWithStudentUtil;

    public Transaction prepareTransaction(User user, Set<Course> courses) {
        int amount = courses.stream().mapToInt(item -> item.getPrice()).sum();

        var transaction = Transaction.builder()
                .user(user)
                .amount(amount)
                .courses(courses)
                .paymentIntentId(UUID.randomUUID().toString())
                .build();
        return transactionRepository.save(transaction);
    }

    public List<Transaction> prepareUniqueCoursesAndTransactions(User user, int count) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var c1 = prepareCourseWithStudentUtil.prepareCourse(user);
            var transaction = Transaction.builder()
                    .user(user)
                    .amount(c1.getPrice())
                    .courses(Set.of(c1))
                    .paymentIntentId(UUID.randomUUID().toString())
                    .build();
            transactions.add(transaction);
        }
        return transactionRepository.saveAll(transactions);
    }

}
