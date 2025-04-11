package ru.eddyz.tgbotdiana.handlers.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.entity.Subscribe;
import ru.eddyz.tgbotdiana.entity.User;
import ru.eddyz.tgbotdiana.exception.UserNotFoundException;
import ru.eddyz.tgbotdiana.handlers.ChatJoinRequestHandler;
import ru.eddyz.tgbotdiana.services.SubscribeService;
import ru.eddyz.tgbotdiana.services.UserService;
import ru.eddyz.tgbotdiana.util.TelegramBotUtil;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class ChatJoinRequestHandlerImpl implements ChatJoinRequestHandler {

    private final UserService userService;
    private final SubscribeService subscribeService;
    private final TelegramClient telegramClient;


    @Override
    public void handle(ChatJoinRequest chatJoinRequest) {
        var chatId = chatJoinRequest.getUser().getId();
        var groupId = chatJoinRequest.getChat().getId();

        try {
            User currentUser = userService.findByTelegramId(chatId);
            if (!currentUser.getIsActive()) {
                sendMessage(chatId, generateBadResponse());
            } else {
                List<Subscribe> activeSubscribes = subscribeService.findByTelegramIdAndGroupIdAndStatus(
                        chatId, groupId, true
                );
                if (!checkSubscribe(activeSubscribes, chatId, groupId)) {
                    TelegramBotUtil.declineUserGroup(groupId, chatId, telegramClient);
                    return;
                }

                TelegramBotUtil.acceptUserGroup(groupId, chatId, telegramClient);
            }


        } catch (UserNotFoundException e) {
            sendMessage(chatId, "Ваш профиль еще не активирован, чтобы активировать профиль введите команду /start");
        }
    }

    private boolean checkSubscribe(List<Subscribe> activeSubscribes, Long chatId, Long groupId) {
        if (activeSubscribes.isEmpty()) {
            sendMessage(chatId, generateBadResponse());
            return false;
        }

        Optional<Subscribe> currentSubscribe = activeSubscribes.stream()
                .filter(s -> s.getGroupTelegramId().equals(groupId))
                .findFirst();

        if (currentSubscribe.isEmpty()) {
            sendMessage(chatId, generateBadResponse());
            return false;
        }
        return true;
    }

    private String generateBadResponse() {
        return "❗У вас нет активных подписок!";
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения, при принятии в группу: ", e);
        }
    }
}
