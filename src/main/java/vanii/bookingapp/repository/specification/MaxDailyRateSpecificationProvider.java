package vanii.bookingapp.repository.specification;

import java.math.BigDecimal;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.SpecificationProvider;

@Component
public class MaxDailyRateSpecificationProvider implements
        SpecificationProvider<Accommodation> {

    @Override
    public String getKey() {
        return "maxDailyRate";
    }

    @Override
    public Specification<Accommodation> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("dailyRate"),
                        Arrays.stream(params)
                            .map(BigDecimal::new)
                            .max(BigDecimal::compareTo)
                            .get()
                );
    }
}
