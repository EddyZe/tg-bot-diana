package ru.eddyz.tgbotdiana.handlers;


import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallBackHandler {
    void handle(CallbackQuery callbackQuery);
}
