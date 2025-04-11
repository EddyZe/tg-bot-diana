package ru.eddyz.tgbotdiana.handlers;


import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;

public interface PreCheckoutQueryHandler {

    void handler(PreCheckoutQuery preCheckoutQuery);
}
