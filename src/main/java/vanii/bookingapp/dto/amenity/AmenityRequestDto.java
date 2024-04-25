package vanii.bookingapp.dto.amenity;

import jakarta.validation.constraints.NotNull;

public record AmenityRequestDto(
        @NotNull String amenity,
        String description){
}
