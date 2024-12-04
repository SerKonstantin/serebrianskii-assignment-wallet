package org.assignment.wallet.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        var connectionFactory = new CachingConnectionFactory(System.getenv().getOrDefault(
                "SPRING_RABBITMQ_HOST",
                "localhost"
            ));
        connectionFactory.setPort(Integer.parseInt(System.getenv().getOrDefault("SPRING_RABBITMQ_PORT", "5672")));

        // Log the connection details
        System.out.println("Connecting to RabbitMQ at "
            + System.getenv().getOrDefault("SPRING_RABBITMQ_HOST", "localhost")
            + System.getenv().getOrDefault("SPRING_RABBITMQ_PORT", "5672"));

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Queue balanceChangeQueue() {
        return new Queue("balanceChangeQueue", true);
    }
}
