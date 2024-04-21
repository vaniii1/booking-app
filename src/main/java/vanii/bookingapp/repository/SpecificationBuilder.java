package vanii.bookingapp.repository;

import org.springframework.data.jpa.domain.Specification;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;

public interface SpecificationBuilder<T> {
    Specification<T> build(AccommodationSearchParameters searchParameters);
}
