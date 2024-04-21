package vanii.bookingapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vanii.bookingapp.config.MapperConfig;
import vanii.bookingapp.dto.amenity.AmenityRequestDto;
import vanii.bookingapp.dto.amenity.AmenityResponseDto;
import vanii.bookingapp.model.Amenity;

@Mapper(config = MapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AmenityMapper {
    Amenity toModel(AmenityRequestDto requestDto);

    AmenityResponseDto toDto(Amenity amenity);

    void updateModel(@MappingTarget Amenity amenity,
                        AmenityRequestDto requestDto);
}
