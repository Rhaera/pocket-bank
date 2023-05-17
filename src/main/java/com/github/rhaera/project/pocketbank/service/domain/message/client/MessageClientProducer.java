package com.github.rhaera.project.pocketbank.service.domain.message.client;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageClientProducer {
    private final String LOANS_EXCHANGE = "loans_exchange";

    private final String LOANS_ROUTING_KEY = "loans_routing_key";

    private final RabbitTemplate rabbitTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClientProducer.class);

    public void sendMessage(String message) {
        LOGGER.info(String.format("MESSAGE SENT: %s", message));
        rabbitTemplate.convertAndSend(LOANS_EXCHANGE, LOANS_ROUTING_KEY, message);
    }
}
