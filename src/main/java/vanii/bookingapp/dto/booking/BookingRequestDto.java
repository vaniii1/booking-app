package vanii.bookingapp.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookingRequestDto(
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDate checkInDate,
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDate checkOutDate,
        @NotNull
        Long accommodationId
) {
}
