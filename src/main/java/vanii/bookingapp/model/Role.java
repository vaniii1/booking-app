package vanii.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@SQLDelete(sql = "UPDATE roles SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleName role;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Override
    public String getAuthority() {
        return role.name();
    }

    public enum RoleName {
        MANAGER,
        CUSTOMER
    }
}
