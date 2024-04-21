package vanii.bookingapp.repository.accommodation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vanii.bookingapp.model.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long>,
        JpaSpecificationExecutor<Accommodation> {

    @Query("FROM Accommodation a JOIN FETCH a.amenities aa WHERE aa.id = :amenityId")
    List<Accommodation> findAccommodationsByAmenityId(Long amenityId);

}
