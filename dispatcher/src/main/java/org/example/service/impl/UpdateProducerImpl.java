package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.service.UpdateProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j
@RequiredArgsConstructor
public class UpdateProducerImpl implements UpdateProducer {//сервис который передает апдейты(смс) в RabbitMQ

    private final RabbitTemplate rabbitTemplate;

    //принимает название очереди и апдейт в виде данных
    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue,update);//передаем в RabbitMQ, update будет преобразован в
        // json, благодаря нами созданному бину в RabbitConfiguration jsonMessageConverter()!
    }
}
