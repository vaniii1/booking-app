package vanii.bookingapp.dto.booking;

import jakarta.validation.constraints.NotNull;
import vanii.bookingapp.model.Booking;

public record UpdateStatusDto(@NotNull Booking.Status status){
}
