package vanii.bookingapp.repository;

import org.springframework.data.jpa.domain.Specification;
import vanii.bookingapp.dto.SearchParametersDto;

public interface SpecificationBuilder<T, S extends SearchParametersDto> {
    Specification<T> build(S searchParameters);
}
