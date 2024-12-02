package org.assignment.wallet.validation.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ScaleValidator implements ConstraintValidator<ValidScale, BigDecimal> {
    private int maxScale;

    @Override
    public void initialize(ValidScale constraintAnnotation) {
        this.maxScale = constraintAnnotation.maxScale();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.scale() <= maxScale;
    }
}
