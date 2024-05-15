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
import vanii.bookingapp.dto.amenity.AmenityRequestDto;
import vanii.bookingapp.dto.amenity.AmenityResponseDto;
import vanii.bookingapp.service.amenity.AmenityService;

@Tag(name = "Amenity Management",
        description = "Endpoints indicate specific actions with Amenities")
@RequiredArgsConstructor
@RestController
@RequestMapping("/amenities")
public class AmenityController {
    private final AmenityService amenityService;

    @Operation(summary = "Create new Amenity",
            description = "Create a new Amenity entity with the defined values")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmenityResponseDto createAmenity(
            @RequestBody @Valid AmenityRequestDto requestDto
    ) {
        return amenityService.save(requestDto);
    }

    @Operation(summary = "Get Amenity",
            description = "Retrieve an Amenity with a specific Id value")
    @GetMapping("/{id}")
    public AmenityResponseDto getAmenityById(@PathVariable Long id) {
        return amenityService.getById(id);
    }

    @Operation(summary = "Get all Amenities",
            description = "Retrieve all Amenities stored in the database")
    @GetMapping
    public List<AmenityResponseDto> getAllAmenities(Pageable pageable) {
        return amenityService.getAll(pageable);
    }

    @Operation(summary = "Update Amenity",
            description = "Update an Amenity with a specific Id value")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}")
    public AmenityResponseDto updateAmenity(@RequestBody AmenityRequestDto requestDto,
                                            @PathVariable Long id) {
        return amenityService.update(requestDto, id);
    }

    @Operation(summary = "Delete Amenity",
            description = "Delete an Amenity with a specific Id value")
    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAmenityById(@PathVariable Long id) {
        amenityService.delete(id);
    }
}
