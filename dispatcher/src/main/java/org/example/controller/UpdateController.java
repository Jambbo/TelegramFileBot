package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Log4j
@RequiredArgsConstructor
public class UpdateController {

    private final MessageUtils messageUtils;
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

        if(update.getMessage()!=null){
            distributeMessagesByType(update);
        }else{
            log.error("Received unsupported message type "+update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if(message.getText()!=null){
            processTextMessage(update);
        }else if(message.getDocument()!=null){
            processDocMessage(update);
        }else if(message.getPhoto()!=null){
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

    private void setView(SendMessage message) {
        telegramBot.sendAnswerMessage(message);
    }

    private void processPhotoMessage(Update update) {

    }

    private void processDocMessage(Update update) {

    }

    private void processTextMessage(Update update) {

    }

}
