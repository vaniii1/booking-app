package vanii.bookingapp.service.booking;

import jakarta.persistence.EntityNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;
import vanii.bookingapp.exception.UnpaidBookingException;
import vanii.bookingapp.mapper.BookingMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.Payment;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;
import vanii.bookingapp.repository.booking.BookingRepository;
import vanii.bookingapp.repository.payment.PaymentRepository;
import vanii.bookingapp.service.accommodation.AccommodationService;
import vanii.bookingapp.service.notification.NotificationService;
import vanii.bookingapp.service.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final NotificationService notificationService;
    private final UserService userService;
    private final AccommodationService accommodationService;
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final PaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto save(BookingRequestDto requestDto) {
        checkIfUserCanCreateNewBooking();
        validateDates(requestDto.checkInDate(), requestDto.checkOutDate());
        Booking booking = bookingMapper.toModel(requestDto);
        booking.setStatus(Booking.Status.PENDING);
        booking.setUser(new User().setId(userService.getCurrentUser().getId()));
        adjustAvailabilityOfAccommodationById(requestDto.accommodationId(), -1);
        Booking savedBooking = bookingRepository.save(booking);
        notificationService.notifyNewBooking(savedBooking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {
        verifyBookingIdForCurrentUser(id);
        return bookingMapper.toDto(getBookingOrThrowException(id));
    }

    @Override
    public List<BookingResponseDto> getBookingsOfCurrentUser() {
        return bookingRepository.getBookingsByUserId(
                userService.getCurrentUser().getId())
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingResponseDto updateMyBooking(BookingRequestDto request, Long id) {
        verifyBookingIdForCurrentUser(id);
        Booking booking = getBookingOrThrowException(id);
        bookingMapper.updateBooking(booking, request);
        handleAccommodationChange(booking, request);
        validateDates(booking.getCheckInDate(), booking.getCheckOutDate());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserIdAndStatus(
            Long userId,
            Booking.Status status
    ) {
        return bookingRepository.getBookingsByUserIdAndStatus(userId, status).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public void updateStatus(Long id, UpdateStatusDto status) {
        Booking booking = getBookingOrThrowException(id);
        Booking.Status newStatus = status.status();
        Booking.Status oldStatus = booking.getStatus();
        Integer oldAvailability = booking.getAccommodation().getAvailability();
        if (newStatus != oldStatus) {
            handleStatusChange(booking, oldStatus, newStatus);
            handleAccommodationNotification(booking.getAccommodation(), oldAvailability);
        }
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
    }

    @Override
    public void delete(Long id) {
        verifyBookingIdForCurrentUser(id);
        Booking booking = getBookingOrThrowException(id);
        if (isPendingOrConfirmed(booking.getStatus())) {
            adjustAvailabilityOfAccommodationById(booking.getAccommodation().getId(), 1);
            notificationService.notifyAccommodationRelease(booking.getAccommodation());
        }
        booking.setStatus(Booking.Status.CANCELED);
        bookingRepository.save(booking);
        notificationService.notifyBookingCancellation(booking);
        bookingRepository.delete(booking);
    }

    @Override
    public Booking getBookingOrThrowException(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find Booking with id: " + id));
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void checkExpiredBookings() {
        List<Booking> list = bookingRepository.getBookingsByPendingAndConfirmedStatuses();
        List<Booking> newExpiredBookings = new ArrayList<>();
        for (Booking booking : list) {
            if (booking.getCheckOutDate().isBefore(LocalDate.now())) {
                booking.setStatus(Booking.Status.EXPIRED);
                notificationService.notifyAccommodationRelease(booking.getAccommodation());
                adjustAvailabilityOfAccommodationById(booking.getAccommodation().getId(), 1);
                bookingRepository.save(booking);
                newExpiredBookings.add(booking);
            }
        }
        if (newExpiredBookings.isEmpty()) {
            notificationService.notifyNoExpiredBookingsToday();
        } else {
            notificationService.notifyExpiredBookings(newExpiredBookings);
        }
    }

    private void adjustAvailabilityOfAccommodationById(Long id, int num) {
        Accommodation accommodation = accommodationService.getAccommodationOrThrowException(id);
        accommodation.adjustAvailability(num);
        accommodationRepository.save(accommodation);
    }

    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (LocalDate.now().isAfter(checkInDate)) {
            throw new DateTimeException("Check-in date cannot be in the past: " + checkInDate);
        }
        if (checkOutDate.isBefore(checkInDate)) {
            throw new DateTimeException("Check-out date cannot be before check-in date: "
                    + checkInDate + " - " + checkOutDate);
        }
    }

    private void verifyBookingIdForCurrentUser(Long bookingId) {
        if (!bookingRepository.existsByUserAndId(userService.getCurrentUser(), bookingId)) {
            throw new AccessDeniedException(
                    "You are not allowed to manage Bookings of other Users. BookingId: " + bookingId
            );
        }
    }

    private void handleAccommodationChange(
            Booking oldBooking,
            BookingRequestDto request
    ) {
        if (!Objects.equals(request.accommodationId(), oldBooking.getAccommodation().getId())
                && isPendingOrConfirmed(oldBooking.getStatus())) {
            adjustAvailabilityOfAccommodationById(oldBooking.getAccommodation().getId(), 1);
            adjustAvailabilityOfAccommodationById(request.accommodationId(), -1);
            notificationService.notifyAccommodationRelease(oldBooking.getAccommodation());
        }
        oldBooking.setAccommodation(accommodationService
                .getAccommodationOrThrowException(request.accommodationId()));
    }

    private void handleStatusChange(
            Booking booking,
            Booking.Status oldStatus,
            Booking.Status newStatus
    ) {
        Long accommodationId = booking.getAccommodation().getId();
        if (isPendingOrConfirmed(oldStatus)) {
            adjustAvailabilityOfAccommodationById(accommodationId, 1);
        }
        if (isPendingOrConfirmed(newStatus)) {
            adjustAvailabilityOfAccommodationById(accommodationId, -1);
        }
    }

    private void handleAccommodationNotification(
            Accommodation accommodation,
            Integer oldAvailability
    ) {
        if (oldAvailability < accommodation.getAvailability()) {
            notificationService.notifyAccommodationRelease(accommodation);
        }
    }

    private boolean isPendingOrConfirmed(Booking.Status status) {
        return status == Booking.Status.PENDING || status == Booking.Status.CONFIRMED;
    }

    private void checkIfUserCanCreateNewBooking() {
        if (paymentRepository.existsByStatusAndUserId(
                Payment.Status.PENDING, userService.getCurrentUser().getId())
        ) {
            throw new UnpaidBookingException("You have an unpaid Booking. Please pay for"
                    + " the existing Booking and then you can create new Booking");
        }
    }
}
