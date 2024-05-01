package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.service.ConsumerService;
import org.example.service.ProducerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import static org.example.RabbitQueue.*;

@Service
@Log4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final ProducerService producerService;
    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE) //означает, что этот метод слушает именно эту очередь
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received.");
        var message = update.getMessage();
        var sendMessage = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Hello from NODE")
                .build();
        producerService.produceAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: Photo message is received.");
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Document message is received.");

    }
}
