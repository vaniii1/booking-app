package vanii.bookingapp.repository;

import liquibase.pro.packaged.I;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.booking.BookingRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/user/add-user.sql",
        "classpath:database/accommodation/add-two-accommodations.sql",
        "classpath:database/booking/add-two-bookings.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/booking/delete-bookings.sql",
        "classpath:database/user/delete-user.sql",
        "classpath:database/accommodation/delete-accommodations.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class BookingRepositoryTest {
    private static final Long ID = 1L;
    @Autowired
    private BookingRepository repository;

    @Test
    @DisplayName("""
            Verify getBookingsByUserId() method works   
            """)
    void getBookingsByUserId_ValidRequest_CorrectResponse() {
        List<Booking> actual = repository.getBookingsByUserId(ID);

        assertEquals(2, actual.size());
        assertEquals(1, actual.get(0).getId());
        assertEquals(Booking.Status.PENDING, actual.get(0).getStatus());
        assertEquals(2, actual.get(1).getId());
        assertEquals(Booking.Status.CANCELED, actual.get(1).getStatus());
    }

    @Test
    @DisplayName("""
            Verify getBookingsByUserIdAndStatus() method works 
            """)
    void getBookingsByUserIdAndStatus_ValidRequest_CorrectResponse() {
        List<Booking> actual = repository.getBookingsByUserIdAndStatus(ID,
                Booking.Status.CANCELED);

        assertEquals(1, actual.size());
        assertEquals(Booking.Status.CANCELED, actual.get(0).getStatus());
        assertEquals(2, actual.get(0).getId());
    }

    @Test
    @DisplayName("""
            Verify getBookingsByPendingAndConfirmedStatuses() method works
            """)
    void getBookingsByPendingAndConfirmedStatuses_ValidRequest_CorrectResponse() {
        List<Booking> actual = repository.getBookingsByPendingAndConfirmedStatuses();

        assertEquals(1, actual.size());
        assertEquals(Booking.Status.PENDING, actual.get(0).getStatus());
        assertEquals(1, actual.get(0).getId());
    }

    @Test
    @DisplayName("""
            Verify existsByUserAndId() method works
            """)
    void existsByUserAndId_ValidRequest_CorrectResponse() {
        User user = new User()
                .setId(ID)
                .setEmail("mail@ua")
                .setFirstName("andrii")
                .setLastName("mak");
        boolean actual = repository.existsByUserAndId(user, ID);

        assertTrue(actual);
    }
}