package vanii.bookingapp.service.amenity;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
        return repository.findById(id).map(mapper::toDto).orElseThrow(() ->
                new EntityNotFoundException("Can't find Amenity with id: " + id));
    }

    @Override
    public List<AmenityResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public AmenityResponseDto update(AmenityRequestDto requestDto, Long id) {
        Optional<Amenity> optionalAmenity = repository.findById(id);
        if (optionalAmenity.isPresent()) {
            Amenity model = optionalAmenity.get();
            mapper.updateModel(model, requestDto);
            return mapper.toDto(repository.save(model));
        }
        throw new EntityNotFoundException("Can't find Amenity with id: " + id);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
