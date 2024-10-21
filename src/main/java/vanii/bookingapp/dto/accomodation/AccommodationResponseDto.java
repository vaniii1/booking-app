package vanii.bookingapp.dto.accomodation;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import vanii.bookingapp.model.Accommodation;

@Data
@Accessors(chain = true)
public class AccommodationResponseDto {
    private Long id;
    private Accommodation.Type type;
    private String location;
    private String size;
    private Set<Long> amenityIds;
    private BigDecimal dailyRate;
    private Integer availability;
}
