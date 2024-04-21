package vanii.bookingapp.repository.specification;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.SpecificationProvider;

@Component
public class TypeSpecificationProvider implements SpecificationProvider<Accommodation> {
    @Override
    public String getKey() {
        return "type";
    }

    @Override
    public Specification<Accommodation> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get("type").in(Arrays.stream(params).toArray());
    }
}
