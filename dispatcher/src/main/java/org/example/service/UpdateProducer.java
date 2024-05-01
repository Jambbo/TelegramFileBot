package org.example.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer { //сервис который передает апдейты в RabbitMQ

    void produce(String rabbitQueue, Update update);

}
