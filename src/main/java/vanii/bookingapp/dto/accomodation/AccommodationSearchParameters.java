package vanii.bookingapp.dto.accomodation;

public record AccommodationSearchParameters(
        String[] type,
        String[] location,
        String[] min_daily_rate,
        String[] max_daily_rate,
        String[] min_available,
        String[] amenities
) {
}
