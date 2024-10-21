package vanii.bookingapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import vanii.bookingapp.model.Payment;
import vanii.bookingapp.repository.payment.PaymentRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/user/add-user.sql",
        "classpath:database/accommodation/add-two-accommodations.sql",
        "classpath:database/booking/add-two-bookings.sql",
        "classpath:database/payment/add-two-payments.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/payment/delete-payments.sql",
        "classpath:database/booking/delete-bookings.sql",
        "classpath:database/user/delete-user.sql",
        "classpath:database/accommodation/delete-accommodations.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class PaymentRepositoryTest {
    private static final Long ID_SEVEN = 7L;
    private static final Long ID_FOUR = 4L;
    @Autowired
    private PaymentRepository repository;

    @Test
    @DisplayName("""
            Verify findBySessionId() method works
            """)
    void findBySessionId_ValidRequest_CorrectResponse() {
        Optional<Payment> actual = repository.findBySessionId("id4770");

        assertNotEquals(Optional.empty(), actual);
        assertEquals("url1007", actual.get().getSessionUrl());
        assertEquals(ID_SEVEN, actual.get().getId());
    }

    @Test
    @DisplayName("""
            Verify getAllByUserId() method works
            """)
    void getAllByUserId_ValidRequest_CorrectResponse() {
        List<Payment> actual = repository.getAllByUserId(ID_FOUR);

        assertEquals(2, actual.size());
        assertEquals("url1007", actual.get(0).getSessionUrl());
        assertEquals("url4322", actual.get(1).getSessionUrl());
    }

    @Test
    @DisplayName("""
            Verify findAllByStatus() method works
            """)
    void findAllByStatus_ValidRequest_CorrectResponse() {
        List<Payment> actual = repository.findAllByStatus(Payment.Status.CANCELED);

        assertEquals(1, actual.size());
        assertEquals("id4770", actual.get(0).getSessionId());
    }

    @Test
    @DisplayName("""
            Verify existsByStatusAndUserId() method works 
            """)
    void existsByStatusAndUserId_ValidRequest_CorrectResponse() {
        boolean actual = repository.existsByStatusAndUserId(Payment.Status.PENDING, ID_FOUR);

        assertFalse(actual);
    }
}
