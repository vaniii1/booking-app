package vanii.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vanii.bookingapp.dto.user.LoginRequestDto;
import vanii.bookingapp.dto.user.LoginResponseDto;
import vanii.bookingapp.dto.user.RegistrationRequestDto;
import vanii.bookingapp.dto.user.RegistrationResponseDto;
import vanii.bookingapp.exception.UserExistsException;
import vanii.bookingapp.security.AuthenticationService;
import vanii.bookingapp.service.user.UserService;

@Tag(name = "User Management",
        description = "Endpoints indicate specific actions with Users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new User",
            description = "Register a new User with unique email address")
    @PostMapping("/register")
    public RegistrationResponseDto registerUser(
            @RequestBody @Valid RegistrationRequestDto requestDto
    ) throws UserExistsException {
        return userService.register(requestDto);
    }

    @Operation(summary = "User Login",
            description = "User login and receiving a jwt in response")
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
