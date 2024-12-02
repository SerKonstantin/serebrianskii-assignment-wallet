package org.assignment.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.assignment.wallet.validation.dto.ValidOperationType;
import org.assignment.wallet.validation.dto.ValidScale;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    @NotNull(message = "Не предоставлен ID кошелька")
    private UUID walletId;

    @ValidOperationType
    private String operationType;

    @NotNull(message = "Не предоставлена сумма")
    @DecimalMin(value = "0", inclusive = false, message = "Сумма должна быть больше 0")
    @ValidScale(maxScale = 2, message = "Сумма не должна содержать более 2 знаков после запятой")
    private BigDecimal amount;
}
