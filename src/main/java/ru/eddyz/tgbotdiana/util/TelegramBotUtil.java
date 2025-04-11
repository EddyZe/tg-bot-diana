package ru.eddyz.tgbotdiana.util;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ApproveChatJoinRequest;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.DeclineChatJoinRequest;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Slf4j
public class TelegramBotUtil {

    public static void unbanUser(Long chatId, Long groupId, TelegramClient telegramClient) {
        try {
            UnbanChatMember unban = UnbanChatMember.builder()
                    .chatId(groupId)
                    .userId(chatId)
                    .build();
            telegramClient.execute(unban);
        } catch (TelegramApiException e) {
            log.error("Ошибка при разблокировке пользователя: ", e);
        }
    }

    public static void banUser(Long chatId, Long groupId, TelegramClient telegramClient) {
        try {
            BanChatMember ban = BanChatMember.builder()
                    .chatId(groupId)
                    .userId(chatId)
                    .build();
            telegramClient.execute(ban);
        } catch (TelegramApiException e) {
            log.error("Ошибка при блокировке пользователя", e);
        }
    }

    public static void acceptUserGroup(Long groupId, Long chatId, TelegramClient telegramClient) {
        try {
            ApproveChatJoinRequest approved = ApproveChatJoinRequest.builder()
                    .chatId(groupId)
                    .userId(chatId)
                    .build();

            telegramClient.execute(approved);
        } catch (TelegramApiException e) {
            log.error("Ошибка при принятии в группу. ", e);
        }
    }

    public static void declineUserGroup(Long groupId, Long chatId, TelegramClient telegramClient) {
        try {
            DeclineChatJoinRequest decline = DeclineChatJoinRequest.builder()
                    .chatId(groupId)
                    .userId(chatId)
                    .build();

            telegramClient.execute(decline);
        } catch (TelegramApiException e) {
            log.error("Ошибка при откланении заявки в группу. ", e);
        }
    }

    public static void deleteMessage(Long chtaId, Integer messageId, TelegramClient telegramClient) {
        try {
            telegramClient.execute(DeleteMessage.builder()
                    .chatId(chtaId)
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения", e);
        }
    }

}
