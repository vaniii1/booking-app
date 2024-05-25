package vanii.bookingapp.service.booking;

import jakarta.persistence.EntityNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;
import vanii.bookingapp.mapper.BookingMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;
import vanii.bookingapp.repository.booking.BookingRepository;
import vanii.bookingapp.service.accommodation.AccommodationService;
import vanii.bookingapp.service.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final AccommodationService accommodationService;
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto save(BookingRequestDto requestDto) {
        checkIfDateIsCorrect(requestDto.checkInDate(), requestDto.checkOutDate());
        Booking booking = bookingMapper.toModel(requestDto);
        booking.setStatus(Booking.Status.PENDING);
        booking.setUser(new User().setId(userService.getCurrentUser().getId()));
        reduceAvailabilityOfAccommodationById(requestDto.accommodationId());
        Booking save = bookingRepository.save(booking);
        return bookingMapper.toDto(save);
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {
        verifyBookingIdForCurrentUser(id);
        changeStatusOfBookingIfNecessary(id);
        return bookingMapper.toDto(getBookingOrThrowException(id));
    }

    @Override
    public List<BookingResponseDto> getBookingsOfCurrentUser() {
        List<Booking> list = bookingRepository.getBookingsByUserId(
                userService.getCurrentUser().getId());
        changeStatusOfBookingListIfNecessary(list);
        return list.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingResponseDto updateMyBooking(BookingRequestDto request, Long id) {
        verifyBookingIdForCurrentUser(id);
        Booking booking = getBookingOrThrowException(id);
        bookingMapper.updateBooking(booking, request);
        handleAccommodationChange(booking, request);
        checkIfDateIsCorrect(booking.getCheckInDate(), booking.getCheckOutDate());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserIdAndStatus(
            Long userId,
            Booking.Status status
    ) {
        List<Booking> list = bookingRepository.getBookingsByUserId(userId);
        changeStatusOfBookingListIfNecessary(list);
        return bookingRepository.getBookingsByUserIdAndStatus(userId, status).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public void updateStatus(Long id, UpdateStatusDto status) {
        Booking booking = getBookingOrThrowException(id);
        Booking.Status newStatus = status.status();
        Booking.Status oldStatus = booking.getStatus();
        if (newStatus != oldStatus) {
            if (isPendingOrConfirmed(oldStatus)
                    && booking.getAccommodation() != null) {
                increaseAvailabilityOfAccommodationById(booking.getAccommodation().getId());
            }
            if (isPendingOrConfirmed(newStatus)
                    && booking.getAccommodation() != null) {
                reduceAvailabilityOfAccommodationById(booking.getAccommodation().getId());
            }
        }
        booking.setStatus(status.status());
        bookingRepository.save(booking);
    }

    @Override
    public void delete(Long id) {
        verifyBookingIdForCurrentUser(id);
        Booking booking = getBookingOrThrowException(id);
        if (isPendingOrConfirmed(booking.getStatus())
                && booking.getAccommodation() != null) {
            increaseAvailabilityOfAccommodationById(booking.getAccommodation().getId());
        }
        bookingRepository.delete(booking);
    }

    @Override
    public Booking getBookingOrThrowException(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find Booking with id: " + id));
    }

    private void changeStatusOfBookingIfNecessary(Long bookingId) {
        Booking booking = getBookingOrThrowException(bookingId);
        booking.changeStatusIfCheckOutDateIsExpired();
        bookingRepository.save(booking);
    }

    private void changeStatusOfBookingListIfNecessary(List<Booking> list) {
        list.stream().map(Booking::getId)
                .forEach(this::changeStatusOfBookingIfNecessary);
    }

    private boolean isPendingOrConfirmed(Booking.Status status) {
        return status == Booking.Status.PENDING || status == Booking.Status.CONFIRMED;
    }

    private void reduceAvailabilityOfAccommodationById(Long id) {
        Accommodation accommodation = accommodationService.getAccommodationOrThrowException(id);
        accommodation.reduceByOne();
        accommodationRepository.save(accommodation);
    }

    private void increaseAvailabilityOfAccommodationById(Long id) {
        Accommodation accommodation = accommodationService.getAccommodationOrThrowException(id);
        accommodation.increaseByOne();
        accommodationRepository.save(accommodation);
    }

    private void checkIfDateIsCorrect(LocalDate checkInDate, LocalDate checkOutDate) {
        if (LocalDate.now().isAfter(checkInDate)) {
            throw new DateTimeException(String.format("""
                    CheckInDate cannot be before today.\s
                    CheckInDate: %s.""", checkInDate));
        }
        if (checkOutDate.isBefore(checkInDate)) {
            throw new DateTimeException(String.format("""
                    CheckOutDate cannot be before checkInDate.\s
                    CheckInDate: %s.\s
                    CheckOutDate: %s.""", checkInDate, checkOutDate));
        }
    }

    private void verifyBookingIdForCurrentUser(Long bookingId) {
        if (!bookingRepository.existsByUserAndId(userService.getCurrentUser(), bookingId)) {
            throw new AccessDeniedException(
                    "You are not allowed to manage Bookings of other Users. BookingId: " + bookingId
            );
        }
    }

    private void handleAccommodationChange(Booking booking, BookingRequestDto request) {
        if (booking.getAccommodation() != null
                && !Objects.equals(request.accommodationId(), booking.getAccommodation().getId())
                && isPendingOrConfirmed(booking.getStatus())) {
            increaseAvailabilityOfAccommodationById(booking.getAccommodation().getId());
            reduceAvailabilityOfAccommodationById(request.accommodationId());
        }
        setAccommodationToBookingById(booking, request.accommodationId());
    }

    private void setAccommodationToBookingById(Booking booking, Long accommodationId) {
        booking.setAccommodation(accommodationService
                .getAccommodationOrThrowException(accommodationId));
    }
}
