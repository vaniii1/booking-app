package vanii.bookingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vanii.bookingapp.controller.AccommodationControllerTest.addAccommodations;
import static vanii.bookingapp.controller.AccommodationControllerTest.deleteAccommodations;
import static vanii.bookingapp.controller.AuthenticationControllerTest.addUser;
import static vanii.bookingapp.controller.AuthenticationControllerTest.deleteUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vanii.bookingapp.dto.booking.BookingRequestDto;
import vanii.bookingapp.dto.booking.BookingResponseDto;
import vanii.bookingapp.dto.booking.UpdateStatusDto;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTest {
    private static final Long ID_FOUR = 4L;
    private static final Long ID_FIVE = 5L;
    private static final Long ID_TEN = 10L;
    private static final Long ID_FIFTEEN = 15L;
    private static BookingResponseDto firstResponse;
    private static BookingResponseDto secondResponse;
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private WebApplicationContext applicationContext;

    @Test
    @DisplayName("""
            Verify createBooking() method works
            """)
    void createBooking_ValidRequest_CorrectResponse() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(
                LocalDate.of(2042, 1, 5),
                LocalDate.of(2055, 3, 1),
                ID_TEN);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/bookings")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingResponseDto.class);

        assertNotNull(actual);
        assertEquals(Booking.Status.PENDING, actual.getStatus());
        assertEquals(requestDto.checkInDate(), actual.getCheckInDate());
        assertEquals(requestDto.checkOutDate(), actual.getCheckOutDate());
        assertEquals(requestDto.accommodationId(), actual.getAccommodationId());
        assertEquals(ID_FOUR, actual.getUserId());
    }

    @Test
    @DisplayName("""
            Verify getBookingById() method works 
            """)
    void getBookingById_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(get("/bookings/5"))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingResponseDto.class);

        assertNotNull(actual);
        assertEquals(firstResponse, actual);
    }

    @Test
    @DisplayName("""
            Verify getAllBookingsOfCurrentUser() method works 
            """)
    void getAllBookingsOfCurrentUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/bookings/my"))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto[] actual = objectMapper.readValue(
                        result.getResponse().getContentAsString(), BookingResponseDto[].class);

        assertEquals(2, actual.length);
        assertEquals(firstResponse, actual[0]);
        assertEquals(secondResponse, actual[1]);
    }

    @Test
    @DisplayName("""
            Verify updateMyBooking() method works 
            """)
    void updateMyBooking_ValidRequest_CorrectResponse() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(
                LocalDate.of(LocalDate.now().getYear() + 1, 1, 1),
                null, null);
        BookingResponseDto expected = new BookingResponseDto()
                .setId(ID_FIVE)
                .setUserId(ID_FOUR)
                .setAccommodationId(ID_FOUR)
                .setStatus(Booking.Status.PENDING)
                .setCheckInDate(requestDto.checkInDate())
                .setCheckOutDate(LocalDate.of(2045, 5, 5));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/bookings/5")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify getBookingsByUserIdAndStatus() method works
            """)
    void getBookingsByUserIdAndStatus_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(get("/bookings?user_id=4&status=PENDING"))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingResponseDto[].class);

        assertEquals(1, actual.length);
        assertEquals(firstResponse, actual[0]);
    }

    @Test
    @DisplayName("""
            Verify updateStatus() method works 
            """)
    void updateStatus_ValidRequest_CorrectResponse() throws Exception {
        UpdateStatusDto status = new UpdateStatusDto(Booking.Status.CONFIRMED);
        String jsonRequest = objectMapper.writeValueAsString(status);
        mockMvc.perform(patch("/bookings/15")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("""
            Verify deleteBookingById() method works  
            """)
    void deleteBookingById_ValidRequest_CorrectResponse() throws Exception {
        mockMvc.perform(delete("/bookings/15"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        firstResponse = new BookingResponseDto()
                .setId(ID_FIVE)
                .setStatus(Booking.Status.PENDING)
                .setUserId(ID_FOUR)
                .setCheckInDate(LocalDate.of(2032, 1, 1))
                .setCheckOutDate(LocalDate.of(2045, 5, 5))
                .setAccommodationId(ID_FOUR);
        secondResponse = new BookingResponseDto()
                .setId(ID_FIFTEEN)
                .setStatus(Booking.Status.CANCELED)
                .setUserId(ID_FOUR)
                .setCheckInDate(LocalDate.of(2033, 5, 1))
                .setCheckOutDate(LocalDate.of(2033, 5, 5))
                .setAccommodationId(ID_TEN);
        User mockUser = new User();
        mockUser.setId(ID_FOUR);
        mockUser.setEmail("email");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null,
                        List.of(new SimpleGrantedAuthority("CUSTOMER"),
                                new SimpleGrantedAuthority("MANAGER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        addUser(dataSource);
        addAccommodations(dataSource);
        addBookings(dataSource);
    }

    @AfterEach
    void tearDown() {
        deleteBookings(dataSource);
        deleteAccommodations(dataSource);
        deleteUser(dataSource);
    }

    @SneakyThrows
    static void addBookings(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/booking/add-two-bookings.sql")
            );
        }
    }

    @SneakyThrows
    static void deleteBookings(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/booking/delete-bookings.sql")
            );
        }
    }
}
