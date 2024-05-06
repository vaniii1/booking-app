package vanii.bookingapp.dto.user;

import org.hibernate.validator.constraints.Length;
import vanii.bookingapp.validation.Email;

public record RegistrationRequestDto(
        @Email
        String email,
        String firstName,
        String lastName,
        @Length(min = 4)
        String password,
        String repeatPassword
){

}
