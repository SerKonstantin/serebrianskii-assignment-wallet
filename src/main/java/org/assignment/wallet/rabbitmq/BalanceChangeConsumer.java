package org.assignment.wallet.rabbitmq;

import org.assignment.wallet.model.Wallet;
import org.assignment.wallet.repository.WalletRepository;
import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.exception.InvalidRequestArgumentException;
import org.assignment.wallet.model.OperationType;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Service
public class BalanceChangeConsumer {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionSaveProducer transactionSaveProducer;

    @RabbitListener(queues = "balanceChangeQueue")
    public void receiveTransaction(TransactionRequestDTO transactionRequest) {
        // Attempt to change the wallet balance
        Wallet wallet = walletRepository.findById(transactionRequest.getWalletId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal newBalance = calculateNewBalance(wallet.getBalance(), transactionRequest);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            // Throw an exception if the balance change fails
            throw new InvalidRequestArgumentException("Insufficient funds for withdrawal");
        }

        // Update wallet balance
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // Send the transaction to the save queue instead of publishing the event
        transactionSaveProducer.sendTransaction(transactionRequest);
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, TransactionRequestDTO request) {
        if (request.getOperationType() == OperationType.WITHDRAW) {
            return currentBalance.subtract(request.getAmount());
        } else if (request.getOperationType() == OperationType.DEPOSIT) {
            return currentBalance.add(request.getAmount());
        }
        return currentBalance;
    }
}
