package vanii.bookingapp.repository.specification;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.SpecificationProvider;

@Component
public class MinAvailableSpecificationProvider implements SpecificationProvider<Accommodation> {
    @Override
    public String getKey() {
        return "minAvailable";
    }

    @Override
    public Specification<Accommodation> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("availability"),
                        Arrays.stream(params)
                                .mapToInt(Integer::valueOf)
                                .boxed()
                                .min(Integer::compareTo)
                                .get()
                );
    }
}
