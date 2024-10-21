package vanii.bookingapp.dto.accomodation;

import java.math.BigDecimal;
import vanii.bookingapp.model.Accommodation;

public record AccommodationWithoutAmenityIdsDto(
        Long id,
        Accommodation.Type type,
        String location,
        String size,
        BigDecimal dailyRate,
        Integer availability) {
}
