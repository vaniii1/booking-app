package vanii.bookingapp.repository.accommodation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vanii.bookingapp.model.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long>,
        JpaSpecificationExecutor<Accommodation> {

    @Query("FROM Accommodation a JOIN FETCH a.amenities aa WHERE aa.id = :amenityId")
    Page<Accommodation> findAccommodationsByAmenityId(Pageable pageable, Long amenityId);
}
