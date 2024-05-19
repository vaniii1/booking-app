package vanii.bookingapp.service.booking;

import java.math.BigDecimal;
import java.util.List;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;
import vanii.bookingapp.model.Booking;

public interface BookingService {
    BookingResponseDto save(BookingRequestDto request);

    BookingResponseDto getBookingById(Long id);

    List<BookingResponseDto> getBookingsOfCurrentUser();

    List<BookingResponseDto> getBookingsByUserIdAndStatus(Long userId, Booking.Status status);

    BookingResponseDto updateMyBooking(BookingRequestDto request, Long id);

    void updateStatus(Long id, UpdateStatusDto status);

    void delete(Long id);

    Booking getBookingOrThrowException(Long bookingId);
}