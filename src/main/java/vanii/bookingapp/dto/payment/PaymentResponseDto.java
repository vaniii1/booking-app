package vanii.bookingapp.dto.payment;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import vanii.bookingapp.model.Payment;

@Data
@Accessors(chain = true)
public class PaymentResponseDto {
    private Long id;
    private Payment.Status status;
    private Long bookingId;
    private Long userId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amount;
}
