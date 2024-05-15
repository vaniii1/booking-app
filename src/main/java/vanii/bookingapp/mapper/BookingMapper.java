package vanii.bookingapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vanii.bookingapp.config.MapperConfig;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.model.Booking;

@Mapper(config = MapperConfig.class, uses = AccommodationMapper.class)
public interface BookingMapper {
    @Mapping(target = "accommodation", source = "accommodationId",
            qualifiedByName = "accommodationById")
    Booking toModel(BookingRequestDto requestDto);

    @Mapping(target = "accommodationId", source = "accommodation.id")
    @Mapping(target = "userId", source = "user.id")
    BookingResponseDto toDto(Booking booking);
}
