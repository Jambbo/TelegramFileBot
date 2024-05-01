package org.example.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

// принимает ответы из RabbitMQ и дальше передает их в UpdateController
public interface AnswerConsumer {

    void consumer(SendMessage message);
}
