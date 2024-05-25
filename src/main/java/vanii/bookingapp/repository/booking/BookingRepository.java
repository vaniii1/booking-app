package vanii.bookingapp.repository.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN FETCH b.user u WHERE u.id = :userId")
    List<Booking> getBookingsByUserId(Long userId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.user u"
            + " WHERE b.status = :status AND u.id = :userId")
    List<Booking> getBookingsByUserIdAndStatus(Long userId, Booking.Status status);

    boolean existsByUserAndId(User user, Long id);
}
