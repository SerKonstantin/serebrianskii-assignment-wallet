// package org.assignment.wallet;

// import org.junit.jupiter.api.Test;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.amqp.core.Queue;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// public class RabbitTest {

//     @Autowired
//     private RabbitTemplate rabbitTemplate;

//     @Autowired
//     private Queue balanceChangeQueue;

//     @Test
//     public void testRabbitMqSendAndReceive() {
//         // Message to send
//         String testMessage = "Test Message";

//         // Send message to queue
//         rabbitTemplate.convertAndSend(balanceChangeQueue.getName(), testMessage);

//         // Receive message from queue
//         String receivedMessage = (String) rabbitTemplate.receiveAndConvert(balanceChangeQueue.getName());

//         // Assert message matches
//         assertNotNull(receivedMessage, "Message should not be null");
//         assertEquals(testMessage, receivedMessage, "Received message should match the sent message");
//     }
// }
