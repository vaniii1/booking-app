package vanii.bookingapp.dto.accomodation;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;

public record AccommodationRequestDto(
        String type,
        String location,
        String size,
        Set<Long> amenityIds,
        @Min(0)
        BigDecimal dailyRate,
        @Min(0)
        Integer availability){
}
