package vanii.bookingapp.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vanii.bookingapp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
