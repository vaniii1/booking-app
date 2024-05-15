package vanii.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;
import vanii.bookingapp.service.booking.BookingService;

@Tag(name = "Booking Management",
        description = "Endpoints indicate specific actions with Bookings")
@RequiredArgsConstructor
@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Create new Booking",
            description = "Create a new Booking entity with the defined values")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @RequestBody @Valid BookingRequestDto requestDto
    ) {
        return bookingService.save(requestDto);
    }

    @Operation(summary = "Get Booking",
            description = "Retrieve a Booking with a specific id value")
    @GetMapping("/{id}")
    public BookingResponseDto getBookingById(
            @PathVariable Long id
    ) {
        return bookingService.getBookingById(id);
    }

    @Operation(summary = "Get all Bookings",
            description = "Retrieve all Bookings of stored in the database")
    @GetMapping
    public List<BookingResponseDto> getAllBookings() {
        return bookingService.getUsersBookings();
    }

    @Operation(summary = "Update Status of Booking",
            description = "Update a Status of Booking with a specific id value")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{id}")
    public void updateStatus(@PathVariable Long id,
                             @RequestBody UpdateStatusDto status) {
        bookingService.updateStatus(id, status);
    }

    @Operation(summary = "Delete Booking",
            description = "Delete a Booking with a specific Id value")
    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public void deleteBookingById(@PathVariable Long id) {
        bookingService.delete(id);
    }
}
