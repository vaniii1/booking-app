package vanii.bookingapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vanii.bookingapp.config.MapperConfig;
import vanii.bookingapp.dto.user.RegistrationRequestDto;
import vanii.bookingapp.dto.user.RegistrationResponseDto;
import vanii.bookingapp.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toModel(RegistrationRequestDto requestDto);

    RegistrationResponseDto toDto(User user);
}
