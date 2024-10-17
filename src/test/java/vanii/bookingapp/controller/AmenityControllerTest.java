package vanii.bookingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
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
import vanii.bookingapp.dto.amenity.AmenityRequestDto;
import vanii.bookingapp.dto.amenity.AmenityResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AmenityControllerTest {
    private static final Long ID_TWO = 2L;
    private static final Long ID_TWENTY = 20L;
    private static AmenityResponseDto firstResponse;
    private static AmenityResponseDto secondResponse;
    private static MockMvc mockMvc;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("""
            Verify createAmenity() method works
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void createAmenity_ValidRequest_CorrectResponse() throws Exception {
        AmenityRequestDto requestDto = new AmenityRequestDto(
                "swimming pool", "deep pool on the backyard");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/amenities")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        AmenityResponseDto actual =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(), AmenityResponseDto.class);

        assertNotNull(actual);
        assertEquals(requestDto.amenity(), actual.amenity());
    }

    @Test
    @DisplayName("""
            Verify getAmenityById() method works
            """)
    @WithMockUser(username = "newuser", roles = "CUSTOMER")
    void getAmenityById_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/amenities/20")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        AmenityResponseDto actual =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(), AmenityResponseDto.class);

        assertNotNull(actual);
        assertEquals(secondResponse, actual);
    }

    @Test
    @DisplayName("""
            Verify getAllAmenities() method works
            """)
    @WithMockUser(username = "newuser", roles = "CUSTOMER")
    void getAllAmenities_ValidRequest_CorrectResponse() throws Exception {
        AmenityResponseDto[] expected = new AmenityResponseDto[] {firstResponse, secondResponse};

        MvcResult result = mockMvc.perform(get("/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AmenityResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AmenityResponseDto[].class);

        assertNotNull(actual);
        assertEquals(2, actual.length);
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

    @Test
    @DisplayName("""
            Verify updateAmenity() method works 
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateAmenity_ValidRequest_CorrectResponse() throws Exception {
        AmenityRequestDto amenityRequestDto = new AmenityRequestDto(null, "works well");
        AmenityResponseDto expected = new AmenityResponseDto(
                ID_TWENTY, secondResponse.amenity(), amenityRequestDto.description());

        String jsonRequest = objectMapper.writeValueAsString(amenityRequestDto);

        MvcResult result = mockMvc.perform(put("/amenities/20")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        AmenityResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AmenityResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify deleteAmenityById() method works
            """)
    @WithMockUser(username = "manager", roles = "MANAGER")
    void deleteAmenityById_ValidRequest_CorrectResponse() throws Exception {
        mockMvc.perform(
                        delete("/amenities/2")
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
        firstResponse = new AmenityResponseDto(ID_TWO, "washing machine", null);
        secondResponse = new AmenityResponseDto(ID_TWENTY, "fridge", null);
        addAmenities(dataSource);
    }

    @AfterEach
    void tearDown() {
        deleteAmenities(dataSource);
    }

    @SneakyThrows
    static void addAmenities(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/amenity/add-two-amenities.sql")
            );
        }
    }

    @SneakyThrows
    static void deleteAmenities(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/amenity/delete-amenities.sql")
            );
        }
    }
}
