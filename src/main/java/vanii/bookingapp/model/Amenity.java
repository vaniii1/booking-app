package vanii.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@SQLDelete(sql = "UPDATE amenities SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotNull
    private String amenity;
    private String description = "";
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public Amenity(Long id) {
        this.id = id;
    }
}
