package vanii.bookingapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vanii.bookingapp.dto.user.LoginRequestDto;
import vanii.bookingapp.dto.user.LoginResponseDto;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponseDto authenticate(LoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.username(), requestDto.password()));
        return new LoginResponseDto(jwtUtil.generateToken(authentication.getName()));
    }
}
