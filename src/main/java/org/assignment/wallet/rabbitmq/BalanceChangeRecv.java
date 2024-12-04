package org.assignment.wallet.rabbitmq;

import org.assignment.wallet.repository.WalletRepository;
import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.exception.InvalidRequestArgumentException;
import org.assignment.wallet.model.OperationType;
import org.assignment.wallet.model.Wallet;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Service
public class BalanceChangeRecv implements ChannelAwareMessageListener {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @RabbitListener(queues = "balanceChangeQueue")
    public void onMessage(@Nullable org.springframework.amqp.core.Message message, @Nullable Channel channel) {
        if (message == null) {
            return;
        }

        try {
            String json = new String(message.getBody());
            var transactionRequest = objectMapper.readValue(json, TransactionRequestDTO.class);

            Wallet wallet = walletRepository.findById(transactionRequest.getWalletId())
                    .orElseThrow(() -> new InvalidRequestArgumentException(
                            "Wallet with ID " + transactionRequest.getWalletId() + " not found"
                    ));

            BigDecimal newBalance = calculateNewBalance(wallet.getBalance(), transactionRequest);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidRequestArgumentException("Insufficient funds.");
            }

            wallet.setBalance(newBalance);
            walletRepository.save(wallet);

            if (channel != null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            if (channel != null) {
                try {
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } catch (Exception ignore) {
                }
            }
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
