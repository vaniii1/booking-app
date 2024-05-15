package vanii.bookingapp.service.booking;

import java.util.List;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;

public interface BookingService {
    BookingResponseDto save(BookingRequestDto requestDto);

    BookingResponseDto getBookingById(Long id);

    List<BookingResponseDto> getUsersBookings();

    void updateStatus(Long id, UpdateStatusDto status);

    void delete(Long id);
}
