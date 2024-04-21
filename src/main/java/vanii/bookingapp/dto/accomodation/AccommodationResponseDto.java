package vanii.bookingapp.dto.accomodation;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class AccommodationResponseDto {
    private Long id;
    private String type;
    private String location;
    private String size;
    private Set<Long> amenityIds;
    private BigDecimal dailyRate;
    private Integer availability;
}
