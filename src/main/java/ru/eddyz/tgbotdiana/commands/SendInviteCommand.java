package ru.eddyz.tgbotdiana.commands;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.domain.CurrentListGroup;
import ru.eddyz.tgbotdiana.domain.Group;
import ru.eddyz.tgbotdiana.entity.Subscribe;
import ru.eddyz.tgbotdiana.entity.User;
import ru.eddyz.tgbotdiana.enums.TypeSubscribe;
import ru.eddyz.tgbotdiana.exception.UserNotFoundException;
import ru.eddyz.tgbotdiana.services.PaymentService;
import ru.eddyz.tgbotdiana.services.SubscribeService;
import ru.eddyz.tgbotdiana.services.UserService;
import ru.eddyz.tgbotdiana.util.TelegramBotUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendInviteCommand implements Command {

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final PaymentService paymentService;
    private final SubscribeService subscribeService;
    private final CurrentListGroup currentListGroup;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public <T extends BotApiObject> void execute(T apiObject) {
        if (apiObject instanceof Message message) {
            if (message.hasSuccessfulPayment())
                responseOnSuccessfulPayment(
                        message.getSuccessfulPayment(),
                        message.getChatId(),
                        message.getFrom().getUserName()
                );
        }
    }

    private void responseOnSuccessfulPayment(SuccessfulPayment successfulPayment, Long chatId, String username) {
        User user = getUser(chatId, username);

        String payload = successfulPayment.getInvoicePayload();
        String[] splt = payload.split(":");
        Long groupTelegramId = Long.parseLong(splt[1]);
        TypeSubscribe typeSubscribe = getTypeSubscribe(splt[2]);
        List<Subscribe> userSubscribe = subscribeService.findByTelegramId(chatId);

        Optional<Subscribe> currentSubscribe = findActiveSubscribe(userSubscribe, groupTelegramId);

        savePayment(successfulPayment, user);

        if (currentSubscribe.isPresent()) {
            extendSubscribe(chatId, user, currentSubscribe.get(), typeSubscribe, groupTelegramId);
            return;
        }

        var endDate = getEndDate(typeSubscribe, LocalDateTime.now());
        var group = currentListGroup.getGroupByTelegramId(groupTelegramId);
        saveNewSubscribe(successfulPayment, group, endDate, user);

        updateUser(user);
        TelegramBotUtil.unbanUser(chatId, groupTelegramId, telegramClient);

        sendMessage(chatId, generateMessage(
                group.getInviteUrl(),
                endDate)
        );
    }

    @NotNull
    private Optional<Subscribe> findActiveSubscribe(List<Subscribe> userSubscribe, Long groupTelegramId) {
        return userSubscribe
                .stream()
                .filter(s -> s.getGroupTelegramId().equals(groupTelegramId) && s.getIsActive())
                .findFirst();
    }

    private void updateUser(User user) {
        user.setIsActive(true);
        userService.update(user);
    }

    private void saveNewSubscribe(SuccessfulPayment successfulPayment, Group group, LocalDateTime endDate, User user) {
        subscribeService.createNewSubscription(
                group.getName(),
                successfulPayment.getTotalAmount() / 100,
                successfulPayment.getCurrency(),
                group.getTelegramId(),
                endDate,
                user);
    }

    private void extendSubscribe(Long chatId, User user, Subscribe currentSubscribe, TypeSubscribe typeSubscribe, Long groupTelegramId) {
        updateUser(user);
        renewSubscriber(chatId, currentSubscribe, typeSubscribe);
        TelegramBotUtil.unbanUser(chatId, groupTelegramId, telegramClient);
    }

    private void savePayment(SuccessfulPayment successfulPayment, User user) {
        paymentService.save(
                successfulPayment.getTotalAmount() / 100,
                successfulPayment.getCurrency(),
                "Покупка/продление подписки.",
                user);
    }

    private String generateMessage(String url, LocalDateTime endDate) {
        return """
                <b>Подписка</b>
                
                Ваша пригласительная ссылка: %s
                Подайте заявку на вступление, она будет одобрена сразу.
                
                Дата окончания подписки: %s
                """.formatted(url, dtf.format(endDate));
    }

    private void renewSubscriber(Long chatId, Subscribe sub, TypeSubscribe typeSubscribe) {
        var endDate = sub.getEndDate();
        endDate = getEndDate(typeSubscribe, endDate);

        sub.setEndDate(endDate);
        sendMessage(chatId, "Вы успешно продлили подписку. Окончание подписки: %s".formatted(dtf.format(endDate)));
        subscribeService.update(sub);
    }

    private LocalDateTime getEndDate(TypeSubscribe typeSubscribe, LocalDateTime endDate) {
        if (typeSubscribe == TypeSubscribe.THREE_MOUTH)
            endDate = endDate.plusMonths(3);
        else if (typeSubscribe == TypeSubscribe.ONE_YEAR)
            endDate = endDate.plusYears(1);
        return endDate;
    }

    private TypeSubscribe getTypeSubscribe(String type) {
        return Arrays.stream(TypeSubscribe.values())
                .filter(s -> s.name().equals(type))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("Type subscribe not found"));
    }

    private User getUser(Long chatId, String username) {
        User user;
        try {
            user = userService.findByTelegramId(chatId);
        } catch (UserNotFoundException e) {
            user = userService.createUser(chatId, username == null ? "Username не указан" : username);
        }
        return user;
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения SendInvite: ", e);
        }
    }
}
