package vanii.bookingapp.repository.accommodation;

import java.nio.file.ProviderNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.repository.SpecificationProvider;
import vanii.bookingapp.repository.SpecificationProviderManager;

@Component
@RequiredArgsConstructor
public class AccommodationSpecificationProviderManager implements
        SpecificationProviderManager<Accommodation> {
    private final List<SpecificationProvider<Accommodation>> specificationProviders;

    @Override
    public SpecificationProvider<Accommodation> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(specProv -> specProv.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new ProviderNotFoundException(
                        "Can't find SpecificationProvider with key: " + key)
                );
    }
}
