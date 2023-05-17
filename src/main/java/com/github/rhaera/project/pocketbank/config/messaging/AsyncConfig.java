package com.github.rhaera.project.pocketbank.config.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {

    private final String LOANS_QUEUE = "loans_queue";
    private final String OFFERS_QUEUE = "offers_queue";
    private final String LOANS_EXCHANGE = "loans_exchange";
    private final String OFFERS_EXCHANGE = "offers_exchange";
    private final String LOANS_ROUTING_KEY = "loans_routing_key";

    @Bean
    public Queue loansQueue() {
        return new Queue(LOANS_QUEUE);
    }

    @Bean
    public Queue offersQueue() {
        return new Queue(OFFERS_QUEUE);
    }

    @Bean
    public TopicExchange loansExchange() {
        return new TopicExchange(LOANS_EXCHANGE);
    }

    @Bean
    public FanoutExchange offersExchange() {
        return new FanoutExchange(OFFERS_EXCHANGE);
    }

    @Bean
    public Binding loansBinding() {
        return BindingBuilder.bind(loansQueue())
                            .to(loansExchange())
                            .with(LOANS_ROUTING_KEY);
    }

    @Bean
    public Binding offersBinding() {
        return BindingBuilder.bind(offersQueue())
                            .to(offersExchange());
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonConverter());
        return rabbitTemplate;
    }
}
