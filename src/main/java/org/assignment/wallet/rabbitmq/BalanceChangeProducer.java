package org.assignment.wallet.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.assignment.wallet.dto.TransactionRequestDTO;

@Service
public class BalanceChangeProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String exchange = "balanceChangeExchange";
    private final String routingKey = "balanceChangeRoutingKey";

    public void sendTransaction(TransactionRequestDTO transactionRequest) {
        rabbitTemplate.convertAndSend(exchange, routingKey, transactionRequest);
    }
}
