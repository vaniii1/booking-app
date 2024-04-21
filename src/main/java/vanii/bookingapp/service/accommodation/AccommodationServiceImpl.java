package vanii.bookingapp.service.accommodation;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;
import vanii.bookingapp.mapper.AccommodationMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;
import vanii.bookingapp.repository.accommodation.AccommodationSpecificationBuilder;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository repository;
    private final AccommodationMapper mapper;
    private final AccommodationSpecificationBuilder specificationBuilder;

    @Override
    public AccommodationResponseDto save(AccommodationRequestDto requestDto) {
        Accommodation model = mapper.toModel(requestDto);
        return mapper.toDto(repository.save(model));
    }

    @Override
    public AccommodationResponseDto getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find Accommodation with id: " + id));
    }

    @Override
    public List<AccommodationResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<AccommodationWithoutAmenityIdsDto> getAllByAmenityId(Long amenityId) {
        return repository.findAccommodationsByAmenityId(amenityId)
                .stream()
                .map(mapper::toDtoWithoutAmenities)
                .toList();
    }

    @Override
    public List<AccommodationResponseDto> search(AccommodationSearchParameters searchParameters) {
        Specification<Accommodation> specification = specificationBuilder.build(searchParameters);
        return repository.findAll(specification)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public AccommodationResponseDto update(AccommodationRequestDto requestDto, Long id) {
        Optional<Accommodation> optionalAccommodation = repository.findById(id);
        if (optionalAccommodation.isPresent()) {
            Accommodation model = optionalAccommodation.get();
            mapper.updateModel(model, requestDto);
            return mapper.toDto(repository.save(model));
        }
        throw new EntityNotFoundException("Can't find Accommodation with id: " + id);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
