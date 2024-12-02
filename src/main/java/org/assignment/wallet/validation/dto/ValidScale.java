package org.assignment.wallet.validation.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ScaleValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScale {
    String message() default "Недопустимое значение";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int maxScale();
}
