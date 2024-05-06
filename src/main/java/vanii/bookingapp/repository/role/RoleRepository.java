package vanii.bookingapp.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import vanii.bookingapp.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
