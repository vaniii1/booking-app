package vanii.bookingapp.service.booking;

import jakarta.persistence.EntityNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;
import vanii.bookingapp.mapper.BookingMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;
import vanii.bookingapp.repository.booking.BookingRepository;
import vanii.bookingapp.service.user.UserService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto save(BookingRequestDto requestDto) {
        checkIfCheckOutDateIsCorrect(requestDto.checkOutDate());
        reduceAvailabilityOfAccommodationById(requestDto.accommodationId());
        Booking booking = bookingMapper.toModel(requestDto);
        booking.setStatus(Booking.Status.PENDING);
        booking.setUser(new User().setId(userService.getCurrentUser().getId()));
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {
        verifyBookingIdForCurrentUser(id);
        changeStatusToExpiredIfNecessaryByBookingId(id);
        return bookingMapper.toDto(getBookingOrThrowException(id));
    }

    @Override
    public List<BookingResponseDto> getUsersBookings() {
        List<Booking> list = bookingRepository
                .getBookingsByUserId(userService.getCurrentUser().getId())
                .stream()
                .toList();
        list.stream()
                .map(Booking::getId)
                .forEach(this::changeStatusToExpiredIfNecessaryByBookingId);
        return list.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public void updateStatus(Long id, UpdateStatusDto status) {
        verifyBookingIdForCurrentUser(id);
        Booking booking = getBookingOrThrowException(id);
        booking.setStatus(status.status());
        bookingRepository.save(booking);
    }

    @Override
    public void delete(Long id) {
        verifyBookingIdForCurrentUser(id);
        Booking booking = getBookingOrThrowException(id);
        if (booking.getAccommodation() != null) {
            increaseAvailabilityOfAccommodationById(booking.getAccommodation().getId());
        }
        bookingRepository.delete(booking);
    }

    private Booking getBookingOrThrowException(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find Booking with id: " + id));
    }

    private Accommodation getAccommodationOrThrowException(Long id) {
        return accommodationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find Accommodation with id: " + id));
    }

    private void reduceAvailabilityOfAccommodationById(Long id) {
        Accommodation accommodation = getAccommodationOrThrowException(id);
        accommodation.reduceByOne();
        accommodationRepository.save(accommodation);
    }

    private void increaseAvailabilityOfAccommodationById(Long id) {
        Accommodation accommodation = getAccommodationOrThrowException(id);
        accommodation.increaseByOne();
        accommodationRepository.save(accommodation);
    }

    private void checkIfCheckOutDateIsCorrect(LocalDate checkOutDate) {
        if (LocalDate.now().isAfter(checkOutDate)) {
            throw new DateTimeException("CheckOutDate can't be before today");
        }
    }

    private void changeStatusToExpiredIfNecessaryByBookingId(Long bookingId) {
        Booking booking = getBookingOrThrowException(bookingId);
        booking.changeStatusIfCheckOutDateIsExpired();
    }

    private void verifyBookingIdForCurrentUser(Long bookingId) {
        if (!bookingRepository.existsByUserAndId(userService.getCurrentUser(), bookingId)) {
            throw new AccessDeniedException(
                    "You are not allowed to manage Bookings of other Users. BookingId: " + bookingId
            );
        }
    }
}
