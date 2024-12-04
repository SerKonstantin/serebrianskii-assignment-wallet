package org.assignment.wallet.rabbitmq;

import org.assignment.wallet.repository.WalletRepository;
import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.exception.InvalidRequestArgumentException;
import org.assignment.wallet.model.OperationType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Service
public class BalanceChangeRecv {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "balanceChangeQueue")
    public void receiveTransaction(String json) {
        try {
            var transactionRequest = objectMapper.readValue(json, TransactionRequestDTO.class);
            var wallet = walletRepository.findById(transactionRequest.getWalletId())
                    .orElseThrow(() -> new InvalidRequestArgumentException(
                            "Кошелек c id " + transactionRequest.getWalletId() + " не найден"
                    ));

            var newBalance = calculateNewBalance(wallet.getBalance(), transactionRequest);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidRequestArgumentException("Недостаточно средств для выполнения операции");
            }

            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось обработать операцию: ", e);
        }
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
