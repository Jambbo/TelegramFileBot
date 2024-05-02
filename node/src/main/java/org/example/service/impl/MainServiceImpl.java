package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dao.RawDataDAO;
import org.example.entity.RawData;
import org.example.service.MainService;
import org.example.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
//главный сервис через который происходит обработка всех входящих сообщений
//роль: связующее звено между базой данных и Consumer`ом( который передает сообщение из брокера)
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var message = update.getMessage();
        var sendMessage = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Hello from NODE")
                .build();
        producerService.produceAnswer(sendMessage);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
