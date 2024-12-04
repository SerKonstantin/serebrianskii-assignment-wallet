package org.assignment.wallet.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.assignment.wallet.dto.TransactionRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BalanceChangeSend {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper om;

    public void sendTransaction(TransactionRequestDTO request) {
        try {
            var json = om.writeValueAsString(request);
            rabbitTemplate.convertAndSend("balanceChangeQueue", json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize TransactionRequestDTO", e);
        }
    }
}
