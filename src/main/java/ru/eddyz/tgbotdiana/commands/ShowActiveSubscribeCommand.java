package ru.eddyz.tgbotdiana.commands;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.entity.Subscribe;
import ru.eddyz.tgbotdiana.services.SubscribeService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowActiveSubscribeCommand implements Command {
    private final TelegramClient telegramClient;
    private final SubscribeService subscribeService;

    @Override
    public <T extends BotApiObject> void execute(T apiObject) {
        if (apiObject instanceof Message message) {
            sendActiveSubscribe(message.getChatId());
        }
    }

    private void sendActiveSubscribe(Long chatId) {
        StringBuilder sb = new StringBuilder("<b>Активные подписки</b>\n\n");

        List<Subscribe> userActiveSubscribe = subscribeService.findUserIsActiveSubscribe(chatId, true);

        if (userActiveSubscribe.isEmpty()) {
            sendMessage(chatId, sb.append("У вас пока нет активных подписок.").toString());
            return;
        }

        userActiveSubscribe.forEach(s -> sb.append(generateSubscribeText(s))
                .append("---------------------------------------\n"));

        sendMessage(chatId, sb.toString());
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке активных подписок", e);
        }
    }

    private String generateSubscribeText(Subscribe subscribe) {
        return """
                <b>%s</b>
                <b>Дата окончания подписки</b>: %s
                """.formatted(
                subscribe.getName(),
                subscribe.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }
}
