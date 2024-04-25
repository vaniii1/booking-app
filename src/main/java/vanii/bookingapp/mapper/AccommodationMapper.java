package vanii.bookingapp.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vanii.bookingapp.config.MapperConfig;
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Amenity;

@Mapper(config = MapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccommodationMapper {
    @Mapping(target = "amenities", ignore = true)
    Accommodation toModel(AccommodationRequestDto requestDto);

    @Mapping(target = "amenityIds", ignore = true)
    AccommodationResponseDto toDto(Accommodation accommodation);

    AccommodationWithoutAmenityIdsDto toDtoWithoutAmenities(Accommodation accommodation);

    void updateModel(@MappingTarget Accommodation accommodation,
                     AccommodationRequestDto requestDto);

    @AfterMapping
    default void setAmenities(@MappingTarget Accommodation accommodation,
                              AccommodationRequestDto requestDto) {
        if (requestDto.amenityIds() != null) {
            Set<Amenity> amenities = requestDto.amenityIds()
                    .stream()
                    .map(id -> {
                        Amenity amenity = new Amenity();
                        amenity.setId(id);
                        return amenity;
                    })
                    .collect(Collectors.toSet());
            accommodation.setAmenities(amenities);
        }
    }

    @AfterMapping
    default void setAmenityIds(@MappingTarget AccommodationResponseDto responseDto,
                              Accommodation accommodation) {
        if (accommodation.getAmenities() != null) {
            Set<Long> amenityIds = accommodation.getAmenities()
                    .stream()
                    .map(Amenity::getId)
                    .collect(Collectors.toSet());
            responseDto.setAmenityIds(amenityIds);
        }
    }
}
