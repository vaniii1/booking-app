package vanii.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import vanii.bookingapp.dto.amenity.AmenityRequestDto;
import vanii.bookingapp.dto.amenity.AmenityResponseDto;
import vanii.bookingapp.mapper.AmenityMapper;
import vanii.bookingapp.model.Amenity;
import vanii.bookingapp.repository.amenity.AmenityRepository;
import vanii.bookingapp.service.amenity.AmenityServiceImpl;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {
    private static AmenityRequestDto requestDto;
    private static Amenity amenity;
    private static AmenityResponseDto expected;

    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private AmenityMapper amenityMapper;
    @InjectMocks
    private AmenityServiceImpl amenityService;

    @BeforeAll
    static void setUp() {
        requestDto = new AmenityRequestDto("football pitch", null);
        amenity = new Amenity().setId(1L).setAmenity(requestDto.amenity());
        expected = new AmenityResponseDto(
                amenity.getId(),
                amenity.getAmenity(),
                amenity.getDescription());
    }

    @Test
    @DisplayName("Verify save() method works")
    void saveAmenity_ValidRequest_CorrectResponse() {
        when(amenityMapper.toModel(requestDto)).thenReturn(amenity);
        when(amenityRepository.save(amenity)).thenReturn(amenity);
        when(amenityMapper.toDto(amenity)).thenReturn(expected);

        AmenityResponseDto actual = amenityService.save(requestDto);

        verifyNoMoreInteractions(amenityMapper, amenityRepository);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify getById() method works")
    void getById_ValidId_CorrectResponseDto() {
        when(amenityRepository.findById(anyLong())).thenReturn(Optional.of(amenity));
        when(amenityMapper.toDto(amenity)).thenReturn(expected);

        AmenityResponseDto actual = amenityService.getById(anyLong());

        verifyNoMoreInteractions(amenityMapper, amenityRepository);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify getById() throws exception")
    void getById_InvalidId_ThrowsException() {
        when(amenityRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> amenityService.getById(amenity.getId())
        );

        String expectedMessage = "Can't find Amenity with id: " + amenity.getId();
        String actualMessage = exception.getMessage();

        verifyNoMoreInteractions(amenityRepository);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Verify getAll() method works")
    void getAll_ValidRequest_CorrectResponse() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Amenity> amenityList = List.of(amenity);
        Page<Amenity> page = new PageImpl<>(amenityList, pageable, amenityList.size());

        when(amenityRepository.findAll(pageable)).thenReturn(page);
        when(amenityMapper.toDto(amenity)).thenReturn(expected);

        List<AmenityResponseDto> actualList = amenityService.getAll(pageable);

        assertEquals(1, actualList.size());
        assertEquals(expected, actualList.get(0));

        verifyNoMoreInteractions(amenityRepository, amenityMapper);
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidRequest_CorrectResponse() {
        AmenityRequestDto updateRequest = new AmenityRequestDto("swimming pool", null);
        Amenity updatedAmenity = new Amenity()
                .setId(amenity.getId())
                .setAmenity(updateRequest.amenity());
        AmenityResponseDto expectedUpdate = new AmenityResponseDto(
                updatedAmenity.getId(),
                updatedAmenity.getAmenity(),
                updatedAmenity.getDescription());
        when(amenityRepository.findById(anyLong())).thenReturn(Optional.of(amenity));
        doNothing().when(amenityMapper).updateAmenity(amenity, updateRequest);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(updatedAmenity);
        when(amenityMapper.toDto(updatedAmenity)).thenReturn(expectedUpdate);

        AmenityResponseDto actual = amenityService.update(updateRequest, anyLong());

        assertEquals(expectedUpdate, actual);
        verifyNoMoreInteractions(amenityRepository, amenityMapper);
    }

    @Test
    @DisplayName("Verify delete() method works")
    void delete_ValidRequest_CorrectHandling() {
        when(amenityRepository.findById(anyLong())).thenReturn(Optional.of(amenity));
        doNothing().when(amenityRepository).deleteById(anyLong());
        amenityService.delete(anyLong());
        verifyNoMoreInteractions(amenityRepository);
    }
}
