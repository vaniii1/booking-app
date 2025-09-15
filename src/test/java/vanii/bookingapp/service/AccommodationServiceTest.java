package vanii.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.data.jpa.domain.Specification;
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;
import vanii.bookingapp.mapper.AccommodationMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;
import vanii.bookingapp.repository.accommodation.AccommodationSpecificationBuilder;
import vanii.bookingapp.repository.amenity.AmenityRepository;
import vanii.bookingapp.service.accommodation.AccommodationServiceImpl;
import vanii.bookingapp.service.notification.NotificationService;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {
    private static final Long ID_ONE = 1L;
    private static AccommodationRequestDto requestDto;
    private static Accommodation accommodation;
    private static AccommodationResponseDto expected;

    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AccommodationSpecificationBuilder specificationBuilder;
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    @DisplayName("Verify save() method works")
    void saveAccommodation_ValidRequest_CorrectResponse() {
        when(accommodationMapper.toModel(requestDto)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        doNothing().when(notificationService).notifyNewAccommodation(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        AccommodationResponseDto actual = accommodationService.save(requestDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(amenityRepository, accommodationMapper,
                accommodationRepository, notificationService);
    }

    @Test
    @DisplayName("Verify getById() method works")
    void getById_ValidRequest_CorrectResponse() {
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        AccommodationResponseDto actual = accommodationService.getById(anyLong());

        assertEquals(expected, actual);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Verify getAll() method works")
    void getAll_ValidRequest_CorrectResponse() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Accommodation> accommodationList = List.of(accommodation);
        Page<Accommodation> page = new PageImpl<>(
                accommodationList, pageable, accommodationList.size());

        when(accommodationRepository.findAll(pageable)).thenReturn(page);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        List<AccommodationResponseDto> actualPage = accommodationService.getAll(pageable);

        assertEquals(1, actualPage.size());
        assertEquals(expected, actualPage.get(0));
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Verify getAllByAmenityId() method works")
    void getAllByAmenityId_ValidRequest_CorrectResponse() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Accommodation> accommodationList = List.of(accommodation);
        Page<Accommodation> page = new PageImpl<>(
                accommodationList, pageable, accommodationList.size());

        AccommodationWithoutAmenityIdsDto expected = new AccommodationWithoutAmenityIdsDto(
                accommodation.getId(),
                accommodation.getType(),
                accommodation.getLocation(),
                accommodation.getSize(),
                accommodation.getDailyRate(),
                accommodation.getAvailability()
        );
        when(accommodationRepository.findAccommodationsByAmenityId(pageable, ID_ONE))
                .thenReturn(page);
        when(accommodationMapper.toDtoWithoutAmenities(accommodation)).thenReturn(expected);

        List<AccommodationWithoutAmenityIdsDto> actualPage =
                accommodationService.getAllByAmenityId(pageable, ID_ONE);

        assertEquals(1, actualPage.size());
        assertEquals(expected, actualPage.get(0));
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Verify search() method works")
    void search_ValidRequest_CorrectResponse() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Accommodation> accommodationList = List.of(accommodation);
        Page<Accommodation> page = new PageImpl<>(
                accommodationList, pageable, accommodationList.size());

        AccommodationSearchParameters searchParameters = new AccommodationSearchParameters()
                .setType(new String[] {"HOUSE"});
        Specification<Accommodation> specification = mock(Specification.class);

        when(specificationBuilder.build(searchParameters)).thenReturn(specification);
        when(accommodationRepository.findAll(specification, pageable)).thenReturn(page);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        List<AccommodationResponseDto> actualPage =
                accommodationService.search(pageable, searchParameters);

        assertEquals(1, actualPage.size());
        assertEquals(expected, actualPage.get(0));
        verifyNoMoreInteractions(
                specificationBuilder, accommodationRepository, accommodationMapper);

    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidRequest_CorrectResponse() {
        AccommodationRequestDto updateRequest = new AccommodationRequestDto(
                "APARTMENT",
                "st bobrova 54",
                "80 m2",
                new HashSet<>(),
                BigDecimal.valueOf(6), 4);
        Accommodation updatedAccommodation = new Accommodation()
                .setId(ID_ONE)
                .setType(Accommodation.Type.APARTMENT)
                .setLocation(updateRequest.location())
                .setSize(updateRequest.size())
                .setAmenities(new HashSet<>())
                .setDailyRate(updateRequest.dailyRate())
                .setAvailability(updateRequest.availability());
        AccommodationResponseDto expectedUpdate = new AccommodationResponseDto()
                .setId(updatedAccommodation.getId())
                .setType(updatedAccommodation.getType())
                .setLocation(updatedAccommodation.getLocation())
                .setSize(updatedAccommodation.getSize())
                .setAmenityIds(new HashSet<>())
                .setDailyRate(updatedAccommodation.getDailyRate())
                .setAvailability(updatedAccommodation.getAvailability());

        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        doNothing().when(accommodationMapper).updateAccommodation(accommodation, updateRequest);
        when(accommodationRepository.save(any(Accommodation.class)))
                .thenReturn(updatedAccommodation);
        when(accommodationMapper.toDto(updatedAccommodation)).thenReturn(expectedUpdate);

        AccommodationResponseDto actual = accommodationService.update(updateRequest, anyLong());

        assertEquals(expectedUpdate, actual);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Verify delete() method works")
    void delete_ValidRequest_CorrectHandling() {
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        doNothing().when(accommodationRepository).deleteById(anyLong());
        accommodationService.delete(anyLong());
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    @DisplayName("Verify getAccommodationOrThrowException() method throws exception")
    void getAccommodationOrThrowException_InvalidRequest_ThrowsException() {
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> accommodationService.getAccommodationOrThrowException(accommodation.getId())
        );

        String expected = "Can't find Accommodation with id: " + accommodation.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    @DisplayName("""
            Verify save() method works
            """)
    void saveAccommodation_InvalidRequest_ThrowsException() {
        AccommodationRequestDto accommodationTemplate = new AccommodationRequestDto(
                "APARTMENT", "raffaello_st. 22", "80 m2",
                Set.of(ID_ONE), BigDecimal.valueOf(15), 1);
        when(amenityRepository.existsById(ID_ONE)).thenReturn(false);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> accommodationService.save(accommodationTemplate)
        );

        String expectedMessage = "Can't find Amenity with id: "
                + ID_ONE;
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @BeforeAll
    static void setUp() {
        requestDto = new AccommodationRequestDto("HOUSE",
                "st strawberry 22",
                "250 m2",
                new HashSet<>(),
                BigDecimal.valueOf(25),
                2);
        accommodation = new Accommodation()
                .setId(ID_ONE)
                .setType(Accommodation.Type.HOUSE)
                .setLocation(requestDto.location())
                .setAmenities(new HashSet<>())
                .setSize(requestDto.size())
                .setDailyRate(requestDto.dailyRate())
                .setAvailability(requestDto.availability());
        expected = new AccommodationResponseDto()
                .setId(accommodation.getId())
                .setType(accommodation.getType())
                .setSize(accommodation.getSize())
                .setAmenityIds(new HashSet<>())
                .setDailyRate(accommodation.getDailyRate())
                .setAvailability(accommodation.getAvailability());
    }

    @AfterEach
    void tearDown() {
        accommodation.setAvailability(2);
    }
}
