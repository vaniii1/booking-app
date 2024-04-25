package vanii.bookingapp.dto.user;

public record RegistrationResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
