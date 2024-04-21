package vanii.bookingapp.repository.amenity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vanii.bookingapp.model.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
