package vanii.bookingapp.service.accommodation;

import java.util.List;
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;

public interface AccommodationService {
    AccommodationResponseDto save(AccommodationRequestDto requestDto);

    AccommodationResponseDto getById(Long id);

    List<AccommodationResponseDto> getAll();

    List<AccommodationWithoutAmenityIdsDto> getAllByAmenityId(Long amenityId);

    List<AccommodationResponseDto> search(AccommodationSearchParameters searchParameters);

    AccommodationResponseDto update(AccommodationRequestDto requestDto, Long id);

    void delete(Long id);
}
