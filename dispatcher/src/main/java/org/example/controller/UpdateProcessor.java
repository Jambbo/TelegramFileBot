package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.service.UpdateProducer;
import org.example.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.RabbitQueue.*;

@Component
@Log4j
@RequiredArgsConstructor
public class UpdateProcessor {

    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;
    private TelegramBot telegramBot; //связываем этот контроллер с телеграм ботом,
    // тут не получится использовать механизм внедрения зависимостей от спринга т.к. приложение не стартанет из-за наличия круговой зависимости

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if(update==null){
            log.error("Received update is null.");
            return;
        }

        if(update.hasMessage()){
            distributeMessagesByType(update);
        }else{
            log.error("Unsupported message type is received: "+update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if(message.hasText()){
            processTextMessage(update);
        }else if(message.hasDocument()){
            processDocMessage(update);
        }else if(message.hasPhoto()){
            processPhotoMessage(update);
        }else{
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(
                update,"Непподерживаемый тип сообщения!");
        setView(sendMessage);
    }
    private void setFileIsReceivedView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(
                update,"Файл получен! Обрабатывается...");
        setView(sendMessage);
    }
    private void setPhotoIsReceivedView(Update update){
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(
                update,"Фото получено! Обрабатывается...");
        setView(sendMessage);
    }

    //использую прокси-метод, а не вызываю выше напрямую у тгбота sendAnswerMessage
    //потому что в updateController передаваться будут также из сервисов, то из сервисов
    // напрямую мы не сможем обращаться к тгботу, нам нужен прокси-метод setView, который пробрасывает ответ дальше в тгбот
    public void setView(SendMessage message) {
        telegramBot.sendAnswerMessage(message);
    }



    //работа методов по передаче каждого отдельного типа данных в нужную очередь ==>
    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE,update);
        setPhotoIsReceivedView(update);//возвращаем пользователю промежуточное смс о том что контент получен и ведется его обработка
    }


    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE,update);
        setFileIsReceivedView(update);//возвращаем пользователю промежуточное смс о том что контент получен и ведется его обработка
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE,update);
    }

}
