package vanii.bookingapp.service.amenity;

import java.util.List;
import vanii.bookingapp.dto.amenity.AmenityRequestDto;
import vanii.bookingapp.dto.amenity.AmenityResponseDto;

public interface AmenityService {
    AmenityResponseDto save(AmenityRequestDto requestDto);

    AmenityResponseDto getById(Long id);

    List<AmenityResponseDto> getAll();

    AmenityResponseDto update(AmenityRequestDto requestDto, Long id);

    void delete(Long id);
}
