package com.github.rhaera.project.pocketbank.service.domain.message.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumerFromClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerFromClient.class);

    @RabbitListener(queues = {"loans_queue"})
    public void receive(String message) {
        LOGGER.info(String.format("MESSAGE RECEIVED: %s", message));

    }
}
