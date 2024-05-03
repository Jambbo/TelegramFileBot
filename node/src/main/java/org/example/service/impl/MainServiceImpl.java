package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.dao.AppUserDAO;
import org.example.dao.RawDataDAO;
import org.example.entity.AppUser;
import org.example.entity.RawData;
import org.example.service.MainService;
import org.example.service.ProducerService;
import org.example.service.enums.ServiceCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.example.entity.enums.UserState.BASIC_STATE;
import static org.example.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static org.example.service.enums.ServiceCommands.*;

//главный сервис через который происходит обработка всех входящих сообщений
//роль: связующее звено между базой данных и Consumer`ом( который передает сообщение из брокера)
@Service
@RequiredArgsConstructor
@Log4j
public class MainServiceImpl implements MainService {
    private final AppUserDAO appUserDAO;
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if(CANCEL.equals(text)){
            output = cancelProcess(appUser);
        }else if(BASIC_STATE.equals(userState)){
            output = processServiceCommand(appUser, text);
        }else if(WAIT_FOR_EMAIL_STATE.equals(userState)){
            //TODO добавить обработку  емейла
        }else{
            log.error("Unknown user state: "+ userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output,chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId,appUser)){
            return;
        }
        //TODO добавить сохранение документа :)
        var answer = "Документ успешно загружен! Ссылка для скачивания: http://test.ru/get-doc/777";
        sendAnswer(answer,chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId,appUser)){
            return;
        }
        //TODO добавить сохранение фото :)
        var answer = "Фото успешно загружено! Ссылка для скачивания: http://test.ru/get-photo/777";
        sendAnswer(answer,chatId);
    }
    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error,chatId);
            return true;
        }else if(!BASIC_STATE.equals(userState)){
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error,chatId);
            return true;
        }
        return false;
    }
    private void sendAnswer(String output, Long chatId) {
        var sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(output)
                .build();
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if(REGISTRATION.equals(cmd)){
        //TODO добавить регистрацию
            return "Временно недоступно.";
        }else if(HELP.equals(cmd)){
            return help();
        }else if(START.equals(cmd)){
            return "Привет! Чтобы посмотреть список доступных команд введите /help";
        }else{
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных комманд:\n"
                +"/cancel - отмена выполнения текующей команды;\n"
                +"/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
            appUser.setState(BASIC_STATE);
            appUserDAO.save(appUser);
            return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();

        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistentAppUser==null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .username(telegramUser.getUserName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }

        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
