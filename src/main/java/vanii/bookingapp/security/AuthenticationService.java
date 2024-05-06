package vanii.bookingapp.security;

import vanii.bookingapp.dto.user.LoginRequestDto;
import vanii.bookingapp.dto.user.LoginResponseDto;

public interface AuthenticationService {

    LoginResponseDto authenticate(LoginRequestDto requestDto);
}
