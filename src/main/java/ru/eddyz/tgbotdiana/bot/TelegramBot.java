package ru.eddyz.tgbotdiana.bot;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.eddyz.tgbotdiana.handlers.CallBackHandler;
import ru.eddyz.tgbotdiana.handlers.ChatJoinRequestHandler;
import ru.eddyz.tgbotdiana.handlers.MessageHandler;
import ru.eddyz.tgbotdiana.handlers.PreCheckoutQueryHandler;


@Getter
@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;

    private final MessageHandler messageHandler;
    private final CallBackHandler callBackHandler;
    private final PreCheckoutQueryHandler preCheckoutQueryHandler;
    private final ChatJoinRequestHandler chatJoinRequestHandler;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       MessageHandler messageHandler, CallBackHandler callBackHandler, PreCheckoutQueryHandler preCheckoutQueryHandler, ChatJoinRequestHandler chatJoinRequestHandler) {
        this.botToken = botToken;
        this.messageHandler = messageHandler;
        this.callBackHandler = callBackHandler;
        this.preCheckoutQueryHandler = preCheckoutQueryHandler;
        this.chatJoinRequestHandler = chatJoinRequestHandler;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            messageHandler.handle(update.getMessage());
            return;
        }

        if (update.hasCallbackQuery()) {
            callBackHandler.handle(update.getCallbackQuery());
            return;
        }

        if (update.hasPreCheckoutQuery()) {
            preCheckoutQueryHandler.handler(update.getPreCheckoutQuery());
            return;
        }

        if (update.hasChatJoinRequest()) {
            chatJoinRequestHandler.handle(update.getChatJoinRequest());
        }
    }
}
