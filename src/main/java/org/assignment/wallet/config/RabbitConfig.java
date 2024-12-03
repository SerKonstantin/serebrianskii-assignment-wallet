package org.assignment.wallet.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange balanceChangeExchange() {
        return new TopicExchange("balanceChangeExchange");
    }

    @Bean
    public Queue balanceChangeQueue() {
        return new Queue("balanceChangeQueue", true);
    }

    @Bean
    public Binding balanceChangeBinding(TopicExchange balanceChangeExchange, Queue balanceChangeQueue) {
        return BindingBuilder.bind(balanceChangeQueue).to(balanceChangeExchange).with("balanceChangeRoutingKey");
    }

    @Bean
    public TopicExchange transactionSaveExchange() {
        return new TopicExchange("transactionSaveExchange");
    }

    @Bean
    public Queue transactionSaveQueue() {
        return new Queue("transactionSaveQueue", true);
    }

    @Bean
    public Binding transactionSaveBinding(TopicExchange transactionSaveExchange, Queue transactionSaveQueue) {
        return BindingBuilder.bind(transactionSaveQueue).to(transactionSaveExchange).with("transactionSaveRoutingKey");
    }
}
