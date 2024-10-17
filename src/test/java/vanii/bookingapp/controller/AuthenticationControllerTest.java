package vanii.bookingapp.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.stream.Stream;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vanii.bookingapp.dto.user.LoginRequestDto;
import vanii.bookingapp.dto.user.LoginResponseDto;
import vanii.bookingapp.dto.user.RegistrationRequestDto;
import vanii.bookingapp.dto.user.RegistrationResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("""
            Verify registerUser() method works 
            """)
    void registerUser_ValidRequest_CorrectResponse() throws Exception {
        RegistrationRequestDto requestDto = new RegistrationRequestDto(
                "a88@mail.ro", "cero", "johnson", "0000", "0000");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/auth/register")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        RegistrationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RegistrationResponseDto.class);

        assertNotNull(actual);
        assertEquals(requestDto.email(), actual.email());
        assertEquals(requestDto.firstName(), actual.firstName());
        assertEquals(requestDto.lastName(), actual.lastName());
    }

    @Test
    @DisplayName("""
            Verify login() method works 
            """)
    void login_ValidRequest_CorrectResponse() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("mail@ua", "1222");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        LoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDto.class);
        assertNotNull(actual);
        assertThat(actual.token().length()).isGreaterThan(1);
        assertThat(Stream.of(actual.token().split(""))
                .filter(chr -> chr.equals(".")).count()).isEqualTo(2);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        addUser(dataSource);
    }

    @AfterEach
    void tearDown() {
        deleteConnections(dataSource);
        deleteUser(dataSource);
    }

    @SneakyThrows
    static void addUser(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/add-user.sql")
            );
        }
    }

    @SneakyThrows
    static void deleteUser(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/delete-user.sql")
            );
        }
    }

    @SneakyThrows
    static void deleteConnections(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/delete-role-connections.sql")
            );
        }
    }
}
