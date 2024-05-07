package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.UpdateProcessor;
import org.example.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.example.RabbitQueue.ANSWER_MESSAGE;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;
    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumer(SendMessage message) {
        updateProcessor.setView(message);
    }
}
