package vanii.bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import vanii.bookingapp.model.Booking;

@Data
@Accessors(chain = true)
public class BookingResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;
    private Long userId;
    private Booking.Status status;
}
