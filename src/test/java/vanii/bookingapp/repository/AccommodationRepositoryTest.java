package vanii.bookingapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.accommodation.AccommodationRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccommodationRepositoryTest {
    private static final Long ID = 2L;
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    @DisplayName("""
            Verify findAccommodationsByAmenityId() method works 
            """)
    @Sql(scripts = {"classpath:database/amenity/add-two-amenities.sql",
            "classpath:database/accommodation/add-two-accommodations.sql",
            "classpath:database/acc_ame/add-amenity-and-accommodation-ids.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/acc_ame/delete-connections-accommodation-amenity.sql",
            "classpath:database/accommodation/delete-accommodations.sql",
            "classpath:database/amenity/delete-amenities.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAccommodationsByAmenityId_ValidRequest_CorrectResponse() {
        Page<Accommodation> actualPage =
                accommodationRepository.findAccommodationsByAmenityId(PageRequest.of(0, 5), ID);

        assertEquals(2, actualPage.toList().size());
        assertEquals("strawberry st. 03", actualPage.toList().get(0).getLocation());
        assertEquals("30m2", actualPage.toList().get(1).getSize());
    }
}
