package vanii.bookingapp.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/user/add-user.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/user/delete-user.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    @DisplayName("""
            Verify findByEmail() method works
            """)
    void findByEmail_ValidRequest_CorrectResponse() {
        Optional<User> actual = repository.findByEmail("mail@ua");

        assertNotEquals(Optional.empty(), actual);
        assertEquals(1, actual.get().getId());
        assertEquals("mak", actual.get().getLastName());
    }
}