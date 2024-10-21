package vanii.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static vanii.bookingapp.model.Role.RoleName.CUSTOMER;
import static vanii.bookingapp.model.Role.RoleName.MANAGER;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vanii.bookingapp.dto.user.RegistrationRequestDto;
import vanii.bookingapp.dto.user.RegistrationResponseDto;
import vanii.bookingapp.mapper.UserMapper;
import vanii.bookingapp.model.Role;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.role.RoleRepository;
import vanii.bookingapp.repository.user.UserRepository;
import vanii.bookingapp.service.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final long LONG_ONE = 1L;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper mapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("""
            Verify register() method works 
            """)
    void register_ValidRequest_CorrectResponse() {
        RegistrationRequestDto requestDto = new RegistrationRequestDto(
                "mail@com", "john", "johnson",
                "1234", "1234");
        User user = new User().setEmail(requestDto.email())
                .setFirstName(requestDto.firstName())
                .setLastName(requestDto.lastName());
        RegistrationResponseDto responseDto = new RegistrationResponseDto(
                LONG_ONE, user.getEmail(), user.getFirstName(), user.getLastName());
        when(userRepository.findByEmail(requestDto.email()))
                .thenReturn(Optional.empty());
        when(mapper.toModel(requestDto)).thenReturn(user);
        when(roleRepository.findAll()).thenReturn(List.of(
                new Role().setRole(CUSTOMER), new Role().setRole(MANAGER)));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(responseDto);

        RegistrationResponseDto actual = userService.register(requestDto);

        assertNotNull(actual);
        assertEquals(responseDto, actual);

        verifyNoMoreInteractions(userRepository, mapper);
    }

    @Test
    @DisplayName("""
            Verify register() method throws Exception 
            """)
    void register_InvalidRequest_ThrowsException() {
        RegistrationRequestDto requestDto = new RegistrationRequestDto(
                "mymail@ua", "ole", "rogstad",
                "0000", "0000");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> userService.register(requestDto)
        );

        String expected = "User with this email already exists. Email: " + requestDto.email();
        String actual = exception.getMessage();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verifyNoMoreInteractions(userRepository);
    }
}
