package vanii.bookingapp.repository.accommodation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vanii.bookingapp.dto.accomodation.AccommodationSearchParameters;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.SpecificationBuilder;
import vanii.bookingapp.repository.SpecificationProviderManager;

@Component
@RequiredArgsConstructor
public class AccommodationSpecificationBuilder implements
        SpecificationBuilder<Accommodation> {
    private final SpecificationProviderManager<Accommodation> manager;

    @Override
    public Specification<Accommodation> build(AccommodationSearchParameters searchParameters) {
        Specification<Accommodation> spec = Specification.where(null);
        if (searchParameters.type() != null
                && searchParameters.type().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("type")
                    .getSpecification(searchParameters.type()));
        }
        if (searchParameters.location() != null
                && searchParameters.location().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("location")
                    .getSpecification(searchParameters.location()));
        }
        if (searchParameters.min_daily_rate() != null
                && searchParameters.min_daily_rate().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("minDailyRate")
                    .getSpecification(searchParameters.min_daily_rate()));
        }
        if (searchParameters.max_daily_rate() != null
                && searchParameters.max_daily_rate().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("maxDailyRate")
                    .getSpecification(searchParameters.max_daily_rate()));
        }
        if (searchParameters.min_available() != null
                && searchParameters.min_available().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("minAvailable")
                    .getSpecification(searchParameters.min_available()));
        }
        return spec;
    }
}
