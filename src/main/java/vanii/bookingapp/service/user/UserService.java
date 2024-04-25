package vanii.bookingapp.service.user;

import vanii.bookingapp.dto.user.RegistrationRequestDto;
import vanii.bookingapp.dto.user.RegistrationResponseDto;
import vanii.bookingapp.exception.UserExistsException;

public interface UserService {
    RegistrationResponseDto register(RegistrationRequestDto requestDto) throws UserExistsException;
}
