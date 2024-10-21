package vanii.bookingapp.service.accommodation;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;
import vanii.bookingapp.mapper.AccommodationMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;
import vanii.bookingapp.repository.accommodation.AccommodationSpecificationBuilder;
import vanii.bookingapp.repository.amenity.AmenityRepository;
import vanii.bookingapp.service.notification.NotificationService;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final NotificationService notificationService;
    private final AccommodationRepository accommodationRepository;
    private final AmenityRepository amenityRepository;
    private final AccommodationMapper mapper;
    private final AccommodationSpecificationBuilder specificationBuilder;

    @Override
    @Transactional
    public AccommodationResponseDto save(AccommodationRequestDto requestDto) {
        if (requestDto.amenityIds() != null) {
            requestDto.amenityIds().forEach(this::verifyValidAmenity);
        }
        Accommodation accommodation = accommodationRepository.save(mapper.toModel(requestDto));
        notificationService.notifyNewAccommodation(accommodation);
        return mapper.toDto(accommodation);
    }

    @Override
    public AccommodationResponseDto getById(Long id) {
        return mapper.toDto(getAccommodationOrThrowException(id));
    }

    @Override
    public List<AccommodationResponseDto> getAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<AccommodationWithoutAmenityIdsDto> getAllByAmenityId(
            Pageable pageable,
            Long amenityId
    ) {
        return accommodationRepository.findAccommodationsByAmenityId(pageable, amenityId)
                .stream()
                .map(mapper::toDtoWithoutAmenities)
                .toList();
    }

    @Override
    public List<AccommodationResponseDto> search(
            Pageable pageable,
            AccommodationSearchParameters searchParameters
    ) {
        Specification<Accommodation> specification = specificationBuilder.build(searchParameters);
        return accommodationRepository.findAll(specification, pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public AccommodationResponseDto update(AccommodationRequestDto requestDto, Long id) {
        Accommodation model = getAccommodationOrThrowException(id);
        mapper.updateAccommodation(model, requestDto);
        return mapper.toDto(accommodationRepository.save(model));
    }

    @Override
    public void delete(Long id) {
        getAccommodationOrThrowException(id);
        accommodationRepository.deleteById(id);
    }

    @Override
    public Accommodation getAccommodationOrThrowException(Long id) {
        return accommodationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find Accommodation with id: " + id));
    }

    private void verifyValidAmenity(Long amenityId) {
        if (!amenityRepository.existsById(amenityId)) {
            throw new EntityNotFoundException("Can't find Amenity with id: "
                    + amenityId);
        }
    }
}
