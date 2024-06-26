package vanii.bookingapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@SQLDelete(sql = "UPDATE bookings SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    @Column(name = "check_in_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate checkInDate;
    @Column(name = "check_out_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate checkOutDate;
    @ManyToOne(cascade = CascadeType.MERGE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
    @OneToMany(mappedBy = "booking", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Payment> payments = new HashSet<>();
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public enum Status {
        PENDING,
        CONFIRMED,
        CANCELED,
        EXPIRED
    }

    public void changeStatusIfCheckOutDateIsExpired() {
        if (status != Status.EXPIRED
                && status != Status.CANCELED
                && LocalDate.now().isAfter(checkInDate)
                && accommodation != null
        ) {
            status = Status.EXPIRED;
            accommodation.adjustAvailability(1);
        }
    }
}
