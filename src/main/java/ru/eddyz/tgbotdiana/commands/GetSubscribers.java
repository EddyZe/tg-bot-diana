package ru.eddyz.tgbotdiana.commands;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.domain.CurrentListGroup;
import ru.eddyz.tgbotdiana.util.InlineKeyUtil;


@Slf4j
@Component
@RequiredArgsConstructor
public class GetSubscribers implements Command {

    private final CurrentListGroup currentListGroup;
    private final TelegramClient telegramClient;

    @Override
    public <T extends BotApiObject> void execute(T apiObject) {
        if (apiObject instanceof Message message) {
            execute(sendSubscribers(message.getChatId()));
        } else if (apiObject instanceof CallbackQuery callbackQuery) {
            execute(editMessage(callbackQuery));
        }
    }

    private SendMessage sendSubscribers(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(generateMessage())
                .parseMode(ParseMode.HTML)
                .replyMarkup(InlineKeyUtil.selectGroupSubscribeKey(currentListGroup.loadGroups()))
                .build();
    }

    private EditMessageReplyMarkup editMessage(CallbackQuery callbackQuery) {
        return EditMessageReplyMarkup.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId())
                .replyMarkup(InlineKeyUtil.selectGroupSubscribeKey(currentListGroup.loadGroups()))
                .build();
    }


    private void execute(BotApiMethod<?> method) {
        try {
            telegramClient.execute(method);
        } catch (TelegramApiException e) {
            log.error("Error while executing command", e);
        }
    }

    private String generateMessage() {
        return """
                <b>Подписка</b>
                
                Выберите подписку, которую хотите подключить 👇""";
    }
}
