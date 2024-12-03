package org.assignment.wallet.event;

import lombok.Getter;
import lombok.AllArgsConstructor;
import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.model.Wallet;

@Getter
@AllArgsConstructor
public class TransactionCreatedEvent {
    private final TransactionRequestDTO request;
    private final Wallet wallet;
}
