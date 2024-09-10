package vanii.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;
import vanii.bookingapp.mapper.BookingMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.Payment;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;
import vanii.bookingapp.repository.booking.BookingRepository;
import vanii.bookingapp.repository.payment.PaymentRepository;
import vanii.bookingapp.service.accommodation.AccommodationService;
import vanii.bookingapp.service.booking.BookingServiceImpl;
import vanii.bookingapp.service.notification.NotificationService;
import vanii.bookingapp.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private static final Long LONG_ONE = 1L;
    private static BookingRequestDto requestDto;
    private static User user;
    private static Accommodation accommodation;
    private static Booking booking;
    private static BookingResponseDto expected;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private AccommodationService accommodationService;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("""
            Verify save() method works
            """)
    void saveBooking_ValidRequest_CorrectResponse() {
        when(paymentRepository.existsByStatusAndUserId(Payment.Status.PENDING, LONG_ONE))
                .thenReturn(false);
        when(bookingMapper.toModel(requestDto)).thenReturn(booking);
        when(userService.getCurrentUser()).thenReturn(user);
        when(accommodationService.getAccommodationOrThrowException(
                anyLong())).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(bookingRepository.save(booking)).thenReturn(booking);
        doNothing().when(notificationService).notifyNewBooking(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        BookingResponseDto actual = bookingService.save(requestDto);

        assertEquals(expected, actual);

        verifyNoMoreInteractions(paymentRepository, bookingMapper, userService,
                accommodationService, accommodationRepository,
                bookingRepository, notificationService);
    }

    @Test
    @DisplayName("""
            Verify getBookingById() method works
            """)
    void getBookingById() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.existsByUserAndId(user, LONG_ONE)).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        BookingResponseDto actual = bookingService.getBookingById(LONG_ONE);
        
        assertEquals(expected, actual);

        verifyNoMoreInteractions(userService, bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("""
            Verify getBookingsOfCurrentUser() method works
            """)
    void getBookingsOfCurrentUser_ValidRequest_CorrectResponse() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.getBookingsByUserId(anyLong())).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        List<BookingResponseDto> actualList = bookingService.getBookingsOfCurrentUser();

        assertEquals(1, actualList.size());
        assertEquals(expected, actualList.get(0));

        verifyNoMoreInteractions(userService, bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("""
            Verify getBookingsByUserIdAndStatus() method works
            """)
    void getBookingsByUserIdAndStatus_ValidRequest_CorrectResponse() {
        when(bookingRepository.getBookingsByUserIdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expected);

        List<BookingResponseDto> actualList = bookingService
                .getBookingsByUserIdAndStatus(LONG_ONE, Booking.Status.PENDING);

        assertEquals(1, actualList.size());
        assertEquals(expected, actualList.get(0));

        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("""
            Verify updateMyBooking() method works
            """)
    void updateMyBooking_ValidRequest_CorrectResponse() {
        BookingRequestDto updateRequest = new BookingRequestDto(LocalDate.now().plusDays(LONG_ONE),
                LocalDate.now().plusMonths(LONG_ONE), LONG_ONE);
        Booking updatedBooking = new Booking()
                .setId(LONG_ONE)
                .setUser(user)
                .setAccommodation(accommodation)
                .setStatus(Booking.Status.PENDING)
                .setCheckInDate(updateRequest.checkInDate())
                .setCheckOutDate(updateRequest.checkOutDate());
        BookingResponseDto expectedUpdate = new BookingResponseDto()
                .setId(updatedBooking.getId())
                .setUserId(LONG_ONE)
                .setAccommodationId(LONG_ONE)
                .setStatus(updatedBooking.getStatus())
                .setCheckInDate(updatedBooking.getCheckInDate())
                .setCheckOutDate(updatedBooking.getCheckOutDate());

        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.existsByUserAndId(user, LONG_ONE)).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        doNothing().when(bookingMapper).updateBooking(booking, updateRequest);
        when(accommodationService.getAccommodationOrThrowException(anyLong()))
                .thenReturn(accommodation);
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
        when(bookingMapper.toDto(updatedBooking)).thenReturn(expectedUpdate);

        BookingResponseDto actual = bookingService.updateMyBooking(updateRequest, LONG_ONE);

        assertEquals(expectedUpdate, actual);

        verifyNoMoreInteractions(bookingRepository, bookingMapper, accommodationService);
    }

    @Test
    @DisplayName("""
            Verify updateStatus() method works 
             """)
    void updateStatus_ValidRequest_CorrectResponse() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(accommodationService.getAccommodationOrThrowException(anyLong()))
                .thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(bookingRepository.save(booking)).thenReturn(booking);

        UpdateStatusDto requestDto = new UpdateStatusDto(Booking.Status.CANCELED);
        bookingService.updateStatus(LONG_ONE, requestDto);

        verifyNoMoreInteractions(bookingRepository, accommodationService, accommodationRepository);
    }

    @Test
    @DisplayName("""
            Verify delete() method works
            """)
    void deleteBooking_ValidRequest_CorrectResponse() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.existsByUserAndId(user, LONG_ONE)).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(accommodationService.getAccommodationOrThrowException(anyLong()))
                .thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        doNothing().when(notificationService).notifyAccommodationRelease(accommodation);
        when(bookingRepository.save(booking)).thenReturn(booking);
        doNothing().when(notificationService).notifyBookingCancellation(booking);
        doNothing().when(bookingRepository).delete(booking);

        bookingService.delete(LONG_ONE);

        verifyNoMoreInteractions(userService, bookingRepository,
                accommodationService, accommodationRepository, notificationService);
    }

    @Test
    @DisplayName("""
            Verify getBookingOrThrowException() method works
            """)
    void getBookingOrThrowException_ValidRequest_CorrectResponse() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking actualBooking = bookingService.getBookingOrThrowException(LONG_ONE);

        assertEquals(booking, actualBooking);

        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("""
            Verify getBookingOrThrowException() method throws exception
            """)
    void getBookingOrThrowException_InvalidRequest_ThrowsException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> bookingService.getBookingOrThrowException(LONG_ONE)
        );

        String expectedMessage = "Can't find Booking with id: " + LONG_ONE;
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("""
            Verify getBookingById() method throws exception
            """)
    void getBookingById_InvalidRequest_ThrowsException() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.existsByUserAndId(user, LONG_ONE)).thenReturn(false);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> bookingService.getBookingById(LONG_ONE)
        );

        String expectedMessage = "You are not allowed to manage Bookings "
                + "of other Users. BookingId: " + LONG_ONE;
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(userService, bookingRepository);
    }

    @Test
    @DisplayName("""
            Verify checkExpiredSessions() method works 
            """)
    void checkExpiredSessions_ExpiredBooking_ChangeStatus() {
        booking.setCheckOutDate(booking.getCheckInDate().minusDays(LONG_ONE));
        when(bookingRepository.getBookingsByPendingAndConfirmedStatuses())
                .thenReturn(List.of(booking));
        doNothing().when(notificationService)
                .notifyAccommodationRelease(booking.getAccommodation());
        when(accommodationService.getAccommodationOrThrowException(LONG_ONE))
                .thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(bookingRepository.save(booking)).thenReturn(booking);
        doNothing().when(notificationService).notifyExpiredBookings(List.of(booking));

        bookingService.checkExpiredBookings();

        verifyNoMoreInteractions(bookingRepository, notificationService);
    }

    @BeforeAll
    static void setUp() {
        requestDto = new BookingRequestDto(LocalDate.now(),
                LocalDate.now().plusDays(LONG_ONE), LONG_ONE);
        user = new User().setId(LONG_ONE);
        accommodation = new Accommodation()
                .setId(LONG_ONE)
                .setAvailability(2);
        booking = new Booking()
                .setId(LONG_ONE)
                .setUser(user)
                .setStatus(Booking.Status.PENDING)
                .setAccommodation(accommodation)
                .setCheckInDate(requestDto.checkInDate())
                .setCheckOutDate(requestDto.checkOutDate());
        expected = new BookingResponseDto()
                .setId(LONG_ONE)
                .setUserId(LONG_ONE)
                .setAccommodationId(LONG_ONE)
                .setStatus(booking.getStatus())
                .setCheckInDate(booking.getCheckInDate())
                .setCheckOutDate(booking.getCheckOutDate());
    }

    @AfterEach
    void tearDown() {
        accommodation.setAvailability(2);
        booking.setCheckOutDate(requestDto.checkOutDate());
        booking.setStatus(Booking.Status.PENDING);
    }
}
