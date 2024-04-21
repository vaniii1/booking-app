package vanii.bookingapp.controller;

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
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;
import vanii.bookingapp.service.accommodation.AccommodationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccommodationResponseDto createAccommodation(
            @RequestBody AccommodationRequestDto requestDto
    ) {
        return accommodationService.save(requestDto);
    }

    @GetMapping("/{id}")
    public AccommodationResponseDto getAccommodationByid(
            @PathVariable Long id
    ) {
        return accommodationService.getById(id);
    }

    @GetMapping
    public List<AccommodationResponseDto> getAllAccommodations() {
        return accommodationService.getAll();
    }

    @GetMapping("/amenity/{amenityId}")
    public List<AccommodationWithoutAmenityIdsDto> getAllAccommodationsByAmenityId(
            @PathVariable Long amenityId) {
        return accommodationService.getAllByAmenityId(amenityId);
    }

    @GetMapping("/search")
    public List<AccommodationResponseDto> searchAccommodations(
            AccommodationSearchParameters searchParameters
    ) {
        return accommodationService.search(searchParameters);
    }

    @PutMapping("/{id}")
    public AccommodationResponseDto updateAccommodation(
            @RequestBody AccommodationRequestDto requestDto,
            @PathVariable Long id
    ) {
        return accommodationService.update(requestDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccommodationById(@PathVariable Long id) {
        accommodationService.delete(id);
    }

}
