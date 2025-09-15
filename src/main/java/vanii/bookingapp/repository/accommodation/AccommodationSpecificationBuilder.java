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
        SpecificationBuilder<Accommodation, AccommodationSearchParameters> {
    private final SpecificationProviderManager<Accommodation> manager;

    @Override
    public Specification<Accommodation> build(AccommodationSearchParameters searchParameters) {
        Specification<Accommodation> spec = Specification.where(null);
        if (searchParameters.getType() != null
                && searchParameters.getType().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("type")
                    .getSpecification(searchParameters.getType()));
        }
        if (searchParameters.getLocation() != null
                && searchParameters.getLocation().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("location")
                    .getSpecification(searchParameters.getLocation()));
        }
        if (searchParameters.getMinDailyRate() != null
                && searchParameters.getMinDailyRate().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("minDailyRate")
                    .getSpecification(searchParameters.getMinDailyRate()));
        }
        if (searchParameters.getMaxDailyRate() != null
                && searchParameters.getMaxDailyRate().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("maxDailyRate")
                    .getSpecification(searchParameters.getMaxDailyRate()));
        }
        if (searchParameters.getMinAvailable() != null
                && searchParameters.getMinAvailable().length > 0) {
            spec = spec.and(manager
                    .getSpecificationProvider("minAvailable")
                    .getSpecification(searchParameters.getMinAvailable()));
        }
        return spec;
    }
}
