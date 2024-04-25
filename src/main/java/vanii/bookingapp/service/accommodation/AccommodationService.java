package vanii.bookingapp.service.accommodation;

import java.util.List;
import org.springframework.data.domain.Pageable;
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;

public interface AccommodationService {
    AccommodationResponseDto save(AccommodationRequestDto requestDto);

    AccommodationResponseDto getById(Long id);

    List<AccommodationResponseDto> getAll(Pageable pageable);

    List<AccommodationWithoutAmenityIdsDto> getAllByAmenityId(Pageable pageable, Long amenityId);

    List<AccommodationResponseDto> search(Pageable pageable,
                                          AccommodationSearchParameters searchParameters);

    AccommodationResponseDto update(AccommodationRequestDto requestDto, Long id);

    void delete(Long id);
}
