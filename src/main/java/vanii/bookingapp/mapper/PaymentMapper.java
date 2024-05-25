package vanii.bookingapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vanii.bookingapp.config.MapperConfig;
import vanii.bookingapp.dto.payment.PaymentResponseDto;
import vanii.bookingapp.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookingId", source = "booking.id")
    PaymentResponseDto toDto(Payment payment);
}
