package vanii.bookingapp.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vanii.bookingapp.dto.amenity.AmenityRequestDto;
import vanii.bookingapp.dto.amenity.AmenityResponseDto;
import vanii.bookingapp.service.amenity.AmenityService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/amenities")
public class AmenityController {
    private final AmenityService amenityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmenityResponseDto createAmenity(@RequestBody
                                            @Valid AmenityRequestDto requestDto) {
        return amenityService.save(requestDto);
    }

    @GetMapping("/{id}")
    public AmenityResponseDto getAmenityById(@PathVariable Long id) {
        return amenityService.getById(id);
    }

    @GetMapping
    public List<AmenityResponseDto> getAllAmenities() {
        return amenityService.getAll();
    }

    @PutMapping("/{id}")
    public AmenityResponseDto updateAmenity(@RequestBody AmenityRequestDto requestDto,
                                            @PathVariable Long id) {
        return amenityService.update(requestDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAmenityById(@PathVariable Long id) {
        amenityService.delete(id);
    }
}
