package org.assignment.wallet.validation.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OperationTypeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOperationType {
    String message() default "Тип операции не поддерживается сервером";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
