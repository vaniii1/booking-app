package vanii.bookingapp.dto.accomodation;

import java.math.BigDecimal;

public record AccommodationWithoutAmenityIdsDto(
        String type,
        String location,
        String size,
        BigDecimal dailyRate,
        Integer availability) {
}
