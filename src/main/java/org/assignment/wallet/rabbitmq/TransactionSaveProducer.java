package org.assignment.wallet.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.assignment.wallet.dto.TransactionRequestDTO;

@Service
public class TransactionSaveProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String exchange = "transactionSaveExchange";
    private final String routingKey = "transactionSaveRoutingKey";

    public void sendTransaction(TransactionRequestDTO transactionRequest) {
        rabbitTemplate.convertAndSend(exchange, routingKey, transactionRequest);
    }
}
