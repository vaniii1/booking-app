package vanii.bookingapp.dto.accomodation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import vanii.bookingapp.validation.AccommodationType;

public record AccommodationRequestDto(
        @NotNull
        @AccommodationType
        String type,
        @NotNull
        String location,
        @NotNull
        String size,
        Set<Long> amenityIds,
        @Min(0)
        @NotNull
        BigDecimal dailyRate,
        @Min(0)
        @NotNull
        Integer availability){
}
