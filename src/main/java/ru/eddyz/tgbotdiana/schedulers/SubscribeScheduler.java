package ru.eddyz.tgbotdiana.schedulers;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.domain.CurrentListGroup;
import ru.eddyz.tgbotdiana.entity.Subscribe;
import ru.eddyz.tgbotdiana.entity.User;
import ru.eddyz.tgbotdiana.services.SubscribeService;
import ru.eddyz.tgbotdiana.util.InlineKeyUtil;
import ru.eddyz.tgbotdiana.util.TelegramBotUtil;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SubscribeScheduler {

    private static final Logger log = LoggerFactory.getLogger(SubscribeScheduler.class);
    private final TelegramClient telegramClient;
    private final SubscribeService subscribeService;
    private final CurrentListGroup currentListGroup;

    @Transactional
    @Scheduled(cron = "1 * * * * *")
    public void checkSubscribers() {
        List<Subscribe> activeSubscribers = subscribeService.findByIsActive(true);
        for (Subscribe subscribe : activeSubscribers) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(subscribe.getEndDate())) {
                User user = subscribe.getUser();
                sendMessage(
                        user.getTelegramId(),
                        generateMessageEndSubscribe(subscribe),
                        InlineKeyUtil.selectGroupSubscribeKey(currentListGroup.loadGroups())
                );

                subscribe.setIsActive(false);
                TelegramBotUtil.banUser(user.getTelegramId(), subscribe.getGroupTelegramId(), telegramClient);
                subscribeService.update(subscribe);
            }
        }
    }

    private void sendMessage(Long chatId, String message, InlineKeyboardMarkup keys) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .replyMarkup(keys)
                    .parseMode(ParseMode.HTML)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в шедулере.", e);
        }
    }

    private String generateMessageEndSubscribe(Subscribe subscribe) {
        return """
                <b>Подписка</b>
                Ваша подписка на <b>%s</b> окончена.
                
                Выберите какую подписку хотите подключить 👇
                """.formatted(subscribe.getName());
    }
}
