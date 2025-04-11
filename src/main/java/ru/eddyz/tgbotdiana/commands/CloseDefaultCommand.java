package ru.eddyz.tgbotdiana.commands;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.util.TelegramBotUtil;

@Component
public class CloseDefaultCommand implements Command {
    private final TelegramClient telegramClient;

    public CloseDefaultCommand(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public <T extends BotApiObject> void execute(T apiObject) {
        if (apiObject instanceof CallbackQuery callbackQuery) {
            TelegramBotUtil.deleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId(),
                    telegramClient
            );
        }
    }
}
