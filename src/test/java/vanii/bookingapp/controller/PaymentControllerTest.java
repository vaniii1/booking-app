package vanii.bookingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vanii.bookingapp.controller.AccommodationControllerTest.addAccommodations;
import static vanii.bookingapp.controller.AccommodationControllerTest.deleteAccommodations;
import static vanii.bookingapp.controller.AuthenticationControllerTest.addUser;
import static vanii.bookingapp.controller.AuthenticationControllerTest.deleteUser;
import static vanii.bookingapp.controller.BookingControllerTest.addBookings;
import static vanii.bookingapp.controller.BookingControllerTest.deleteBookings;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
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
import vanii.bookingapp.dto.payment.PaymentRequestDto;
import vanii.bookingapp.dto.payment.PaymentResponseDto;
import vanii.bookingapp.model.Payment;
import vanii.bookingapp.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {
    private static final Long ID_FOUR = 4L;
    private static final Long ID_FIVE = 5L;
    private static final Long ID_SEVEN = 7L;
    private static final Long ID_NINE = 9L;
    private static final Long ID_FIFTEEN = 15L;
    private static PaymentResponseDto firstResponse;
    private static PaymentResponseDto secondResponse;
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private WebApplicationContext applicationContext;

    @Test
    @DisplayName("""
            Verify createBookingPayment() method works 
            """)
    void createBookingPayment_ValidRequest_CorrectResponse() throws Exception {
        PaymentRequestDto requestDto = new PaymentRequestDto(ID_FIFTEEN);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class);
        BigDecimal expectedAmount = new BigDecimal(120);

        assertNotNull(actual);
        assertEquals(expectedAmount, actual.getAmount());
        assertEquals(Payment.Status.PENDING, actual.getStatus());
        assertEquals(ID_FIFTEEN, actual.getBookingId());
        assertEquals(ID_FOUR, actual.getUserId());
    }

    @Test
    @DisplayName("""
            Verify successPayment() method works 
            """)
    void successPayment_ValidRequest_CorrectResponse() throws Exception {
        PaymentResponseDto expected = new PaymentResponseDto()
                .setId(ID_SEVEN)
                .setAmount(new BigDecimal(20))
                .setSessionUrl("url1007")
                .setSessionId("id4770")
                .setStatus(Payment.Status.PAID)
                .setUserId(ID_FOUR)
                .setBookingId(ID_FIVE);
        MvcResult result = mockMvc.perform(put("/payments/success?session_id=id4770"))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify cancelPayment() method works 
            """)
    void cancelPayment_ValidRequest_CorrectResponse() throws Exception {
        String expected = "Payment was Canceled. Please try again later.";
        MvcResult result = mockMvc.perform(put("/payments/cancel?session_id=id7007"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(result);
        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("""
            Verify renewPayment() method works 
            """)
    void renewPayment_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(post("/payments/renew?session_id=id7007"))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class);

        assertNotNull(actual);
        assertEquals(ID_NINE, actual.getId());
        assertEquals(Payment.Status.PENDING, actual.getStatus());
        assertNotEquals("url4322", actual.getSessionUrl());
        assertNotEquals("id7007", actual.getSessionId());
    }

    @Test
    @DisplayName("""
            Verify getPaymentsForCurrentUser() method works  
            """)
    void getPaymentsForCurrentUser_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments/my"))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto[].class);

        assertNotNull(actual);
        assertEquals(firstResponse, actual[0]);
        assertEquals(secondResponse, actual[1]);
    }

    @Test
    @DisplayName("""
            Verify getPaymentsForCertainUser() method works
            """)
    void getPaymentsForCertainUser_ValidRequest_CorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments?user_id=4"))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto[].class);

        assertNotNull(actual);
        assertEquals(firstResponse, actual[0]);
        assertEquals(secondResponse, actual[1]);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        firstResponse = new PaymentResponseDto()
                .setId(ID_SEVEN)
                .setStatus(Payment.Status.CANCELED)
                .setUserId(ID_FOUR)
                .setBookingId(ID_FIVE)
                .setAmount(new BigDecimal(20))
                .setSessionUrl("url1007")
                .setSessionId("id4770");
        secondResponse = new PaymentResponseDto()
                .setId(ID_NINE)
                .setStatus(Payment.Status.EXPIRED)
                .setUserId(ID_FOUR)
                .setBookingId(ID_FIFTEEN)
                .setAmount(new BigDecimal(50))
                .setSessionUrl("url4322")
                .setSessionId("id7007");
        User mockUser = new User();
        mockUser.setId(ID_FOUR);
        mockUser.setEmail("email");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null,
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"),
                                new SimpleGrantedAuthority("ROLE_MANAGER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        addUser(dataSource);
        addAccommodations(dataSource);
        addBookings(dataSource);
        addPayments(dataSource);
    }

    @AfterEach
    void tearDown() {
        deletePayments(dataSource);
        deleteBookings(dataSource);
        deleteAccommodations(dataSource);
        deleteUser(dataSource);
    }

    @SneakyThrows
    static void addPayments(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/payment/add-two-payments.sql")
            );
        }
    }

    @SneakyThrows
    static void deletePayments(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/payment/delete-payments.sql")
            );
        }
    }
}
