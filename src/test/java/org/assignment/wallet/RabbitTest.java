package org.assignment.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.model.OperationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;

// Unit test to understand rabbitmq flow

@SpringBootTest
@ActiveProfiles("test")
public class RabbitTest {

    private static final String QUEUE_NAME = "balanceChangeQueue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    @Test
    public void testRabbitMqSendAndReceive() throws Exception {
        var request = new TransactionRequestDTO();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("100.00"));

        // Was able to send only Strings or Bytes
        var om = new ObjectMapper();
        String dataString = om.writeValueAsString(request);

        rabbitTemplate.convertAndSend(QUEUE_NAME, dataString); // Send to queue

        var receivedString = rabbitTemplate.receiveAndConvert(QUEUE_NAME).toString(); // Receive from queue

        var receivedRequest = om.readValue(receivedString, TransactionRequestDTO.class);

        assertEquals(request.getWalletId(), receivedRequest.getWalletId());
        assertEquals(request.getOperationType(), receivedRequest.getOperationType());
        assertEquals(request.getAmount(), receivedRequest.getAmount());
    }

    @Configuration
    static class RabbitConfig {
        @Bean
        public ConnectionFactory connectionFactory() {
            // TODO: Change, direct input is fragile
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
            connectionFactory.setPort(5672);
            return connectionFactory;
        }

        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            return new RabbitTemplate(connectionFactory);
        }

        @Bean
        public Queue balanceChangeQueue() {
            return new Queue(QUEUE_NAME, false);
        }
    }
}
