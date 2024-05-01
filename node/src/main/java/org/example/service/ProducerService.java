package org.example.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//этот сервис нужен для отправки ответов с ноды в брокер
public interface ProducerService {
    //всего 1 метод с однотипным аргументом, это будет просто текстовое сообщение с указанием номера чата пользователя
    void produceAnswer(SendMessage sendMessage);

}
