package vanii.bookingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vanii.bookingapp.controller.AmenityControllerTest.addAmenities;
import static vanii.bookingapp.controller.AmenityControllerTest.deleteAmenities;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vanii.bookingapp.dto.accomodation.AccommodationRequestDto;
import vanii.bookingapp.dto.accomodation.AccommodationResponseDto;
import vanii.bookingapp.dto.accomodation.AccommodationWithoutAmenityIdsDto;
import vanii.bookingapp.model.Accommodation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccommodationControllerTest {
    private static final Long ID_TWO = 2L;
    private static final Long ID_FOUR = 4L;
    private static final Long ID_TEN = 10L;
    private static final Long ID_TWENTY = 20L;
    private static AccommodationResponseDto firstResponse;
    private static AccommodationResponseDto secondResponse;
    private static MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("""
            Verify createAccommodation() method works 
            """)
    @WithMockUser(username = "manager", roles = "MANAGER")
    void createAccommodation_ValidRequest_CorrectResponse() throws Exception {
        AccommodationRequestDto requestDto = new AccommodationRequestDto(
                "CONDO", "lovely st. 22", "50m2", null, BigDecimal.TEN, 2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/accommodations")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);

        assertNotNull(actual);
        assertEquals(requestDto.location(), actual.getLocation());
        assertNotNull(actual.getAmenityIds());
        assertEquals(requestDto.size(), actual.getSize());
    }

    @Test
    @DisplayName("""
            Verify getAccommodationById() method works
            """)
    @WithMockUser(username = "user", roles = "CUSTOMER")
    void getAccommodationById_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/accommodations/10")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);

        assertNotNull(actual);
        assertEquals(secondResponse, actual);
    }

    @Test
    @DisplayName("""
            Verify getAllAccommodations() method works 
            """)
    @WithMockUser(username = "bob", roles = "CUSTOMER")
    void getAllAccommodations_ValidRequest_CorrectResponse() throws Exception {
        AccommodationResponseDto[] expected =
                new AccommodationResponseDto[] {firstResponse, secondResponse};
        MvcResult result = mockMvc.perform(
                        get("/accommodations")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        AccommodationResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto[].class);

        assertEquals(2, actual.length);
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

    @Test
    @DisplayName("""
            Verify getAllAccommodationsByAmenityId() method works 
            """)
    @WithMockUser(username = "newuser", roles = "CUSTOMER")
    void getAllAccommodationsByAmenityId_ValidRequest_CorrectResponse() throws Exception {
        AccommodationWithoutAmenityIdsDto expected = new AccommodationWithoutAmenityIdsDto(
                firstResponse.getId(), firstResponse.getType(),
                firstResponse.getLocation(), firstResponse.getSize(),
                firstResponse.getDailyRate(), firstResponse.getAvailability());
        MvcResult result = mockMvc.perform(
                        get("/accommodations/amenity/20")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationWithoutAmenityIdsDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationWithoutAmenityIdsDto[].class);

        assertEquals(1, actual.length);
        assertEquals(expected, actual[0]);
    }

    @Test
    @DisplayName("""
            Verify searchAccommodations() method works
            """)
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    void searchAccommodations_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/accommodations/search?type=CONDO")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();
        AccommodationResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto[].class);

        assertEquals(1, actual.length);
        assertEquals(secondResponse, actual[0]);
    }

    @Test
    @DisplayName("""
            Verify updateAccommodation() method works 
            """)
    @WithMockUser(username = "john", roles = "MANAGER")
    void updateAccommodation_ValidRequest_CorrectResponse() throws Exception {
        AccommodationRequestDto updateRequest = new AccommodationRequestDto(
                "HOUSE", "wooden fridge st. 80",
                null, null, null, null);
        AccommodationResponseDto expected = new AccommodationResponseDto()
                .setId(ID_TEN)
                .setType(Accommodation.Type.HOUSE)
                .setSize("30m2")
                .setAmenityIds(Set.of(ID_TWO))
                .setLocation(updateRequest.location())
                .setDailyRate(new BigDecimal(30))
                .setAvailability(2);
        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        MvcResult result = mockMvc.perform(
                put("/accommodations/10")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify deleteAccommodationById() method works 
            """)
    @WithMockUser(username = "martin", roles = "MANAGER")
    void deleteAccommodationById_ValidRequest_CorrectResponse() throws Exception {
        mockMvc.perform(
                delete("/accommodations/10")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        firstResponse = new AccommodationResponseDto()
                .setId(ID_FOUR)
                .setSize("44m2")
                .setType(Accommodation.Type.APARTMENT)
                .setLocation("strawberry st. 03")
                .setDailyRate(BigDecimal.valueOf(40))
                .setAvailability(1)
                .setAmenityIds(Set.of(ID_TWO, ID_TWENTY));
        secondResponse = new AccommodationResponseDto()
                .setId(ID_TEN)
                .setSize("30m2")
                .setType(Accommodation.Type.CONDO)
                .setLocation("blackberry st. 07")
                .setAmenityIds(new HashSet<>())
                .setDailyRate(BigDecimal.valueOf(30))
                .setAvailability(2)
                .setAmenityIds(Set.of(ID_TWO));
        addAmenities(dataSource);
        addAccommodations(dataSource);
        addAccommodationAmenityConnections(dataSource);
    }

    @AfterEach
    void tearDown() {
        deleteAccommodationAmenityConnections(dataSource);
        deleteAmenities(dataSource);
        deleteAccommodations(dataSource);
    }

    @SneakyThrows
    static void addAccommodations(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/add-two-accommodations.sql")
            );
        }
    }

    @SneakyThrows
    static void deleteAccommodations(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/accommodation/delete-accommodations.sql")
            );
        }
    }

    @SneakyThrows
    static void addAccommodationAmenityConnections(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/acc_ame/add-amenity-and-accommodation-ids.sql")
            );
        }
    }

    @SneakyThrows
    static void deleteAccommodationAmenityConnections(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/acc_ame/delete-connections-accommodation-amenity.sql"));
        }
    }
}
