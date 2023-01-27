package com.telegram.bot.service;

import com.telegram.bot.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.telegram.bot.buttons.Buttons.inlineMarkup;

@Component
@Slf4j
public class Service extends TelegramLongPollingBot {

    @Autowired
    Config config;

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotoken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        Long chatId;
        String userName;
        String receivedMessage;

        //если получено сообщение текстом
        if (update.hasMessage() && update.getMessage().hasText()) {

            chatId = update.getMessage().getChatId();
            userName = update.getMessage().getFrom().getFirstName();

            receivedMessage = update.getMessage().getText();
            receivedMessage = update.getMessage().getText(); // в эту переменную текст входящего сообщения

            botAnswerUtils(receivedMessage, chatId, userName);
            log.info("Replied to user " + update.getMessage().getChat().getFirstName() + " " + receivedMessage);
        }

        //если нажата одна из кнопок бота
        else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, userName);
            log.info("Replied to user " + update.getCallbackQuery().getFrom().getUserName());

        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage) {
            case "/start" -> startBot(chatId, userName);
            case "/help" -> sendHelpText(chatId, "HELP_TEXT");
            default -> sendAnotherText(chatId, userName + ", нажми кнопку, балда");
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(inlineMarkup());

        try {
            execute(message); // здесть отправляем приветственное сообщение
            log.info(message.getText() + "Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    private void sendHelpText(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());

        }
    }

    private void sendAnotherText(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(inlineMarkup());

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}