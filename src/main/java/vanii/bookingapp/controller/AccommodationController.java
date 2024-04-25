package vanii.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Tag(name = "Accommodation Management",
        description = "Endpoints indicate specific actions with accommodations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @Operation(summary = "Create new Accommodation",
            description = "Create a new Accommodation entity with the defined values")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccommodationResponseDto createAccommodation(
            @RequestBody @Valid AccommodationRequestDto requestDto
    ) {
        return accommodationService.save(requestDto);
    }

    @Operation(summary = "Get Accommodation",
            description = "Retrieve an accommodation with a specific id value")
    @GetMapping("/{id}")
    public AccommodationResponseDto getAccommodationById(
            @PathVariable Long id
    ) {
        return accommodationService.getById(id);
    }

    @Operation(summary = "Get all Accommodations",
            description = "Retrieve all accommodations stored in the database")
    @GetMapping
    public List<AccommodationResponseDto> getAllAccommodations(Pageable pageable) {
        return accommodationService.getAll(pageable);
    }

    @Operation(summary = "Get Accommodations by AmenityId",
            description = "Retrieve all accommodations that have a certain amenity")
    @GetMapping("/amenity/{amenityId}")
    public List<AccommodationWithoutAmenityIdsDto> getAllAccommodationsByAmenityId(
            Pageable pageable,
            @PathVariable Long amenityId
    ) {
        return accommodationService.getAllByAmenityId(pageable, amenityId);
    }

    @Operation(summary = "Search through Accommodations",
            description = "Retrieve all accommodations with specific search parameters")
    @GetMapping("/search")
    public List<AccommodationResponseDto> searchAccommodations(
            Pageable pageable,
            AccommodationSearchParameters searchParameters
    ) {
        return accommodationService.search(pageable, searchParameters);
    }

    @Operation(summary = "Update Accommodation",
            description = "Update an accommodation with a specific id value")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}")
    public AccommodationResponseDto updateAccommodation(
            @RequestBody AccommodationRequestDto requestDto,
            @PathVariable Long id
    ) {
        return accommodationService.update(requestDto, id);
    }

    @Operation(summary = "Delete Accommodation",
            description = "Delete an accommodation with a specific id value")
    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccommodationById(@PathVariable Long id) {
        accommodationService.delete(id);
    }

}
