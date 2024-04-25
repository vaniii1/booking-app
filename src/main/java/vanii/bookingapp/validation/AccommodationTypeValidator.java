package vanii.bookingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;
import vanii.bookingapp.model.Accommodation;

public class AccommodationTypeValidator implements ConstraintValidator<AccommodationType, String> {
    private final Accommodation.Type[] types = Accommodation.Type.values();

    @Override
    public boolean isValid(String type, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = Arrays.stream(types)
                .map(Enum::name)
                .anyMatch(name -> name.equals(type));

        if (!isValid) {
            String validTypes = Arrays.stream(types)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "must be one of these: " + validTypes)
                    .addConstraintViolation();
        }
        return isValid;
    }
}
