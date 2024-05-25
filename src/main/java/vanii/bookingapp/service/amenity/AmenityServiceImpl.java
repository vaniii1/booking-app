package vanii.bookingapp.service.amenity;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vanii.bookingapp.dto.amenity.AmenityRequestDto;
import vanii.bookingapp.dto.amenity.AmenityResponseDto;
import vanii.bookingapp.mapper.AmenityMapper;
import vanii.bookingapp.model.Amenity;
import vanii.bookingapp.repository.amenity.AmenityRepository;

@RequiredArgsConstructor
@Service
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository repository;
    private final AmenityMapper mapper;

    @Override
    public AmenityResponseDto save(AmenityRequestDto requestDto) {
        Amenity model = mapper.toModel(requestDto);
        return mapper.toDto(repository.save(model));
    }

    @Override
    public AmenityResponseDto getById(Long id) {
        return mapper.toDto(getAmenityOrThrowException(id));
    }

    @Override
    public List<AmenityResponseDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public AmenityResponseDto update(AmenityRequestDto requestDto, Long id) {
        Amenity model = getAmenityOrThrowException(id);
        mapper.updateAmenity(model, requestDto);
        return mapper.toDto(repository.save(model));
    }

    @Override
    public void delete(Long id) {
        getAmenityOrThrowException(id);
        repository.deleteById(id);
    }

    private Amenity getAmenityOrThrowException(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find Amenity with id: " + id));
    }
}
