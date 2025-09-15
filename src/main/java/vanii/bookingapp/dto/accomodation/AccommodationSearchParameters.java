package vanii.bookingapp.dto.accomodation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import vanii.bookingapp.dto.SearchParametersDto;

@Data
@Accessors(chain = true)
public class AccommodationSearchParameters extends SearchParametersDto {
    private String[] type;
    private String[] location;
    @JsonProperty("min_daily_rate")
    private String[] minDailyRate;
    @JsonProperty("max_daily_rate")
    private String[] maxDailyRate;
    @JsonProperty("min_available")
    private String[] minAvailable;
    private String[] amenities;
}
