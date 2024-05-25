package vanii.bookingapp.repository.payment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vanii.bookingapp.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.user u WHERE u.id = :userId")
    List<Payment> getAllByUserId(Long userId);
}
