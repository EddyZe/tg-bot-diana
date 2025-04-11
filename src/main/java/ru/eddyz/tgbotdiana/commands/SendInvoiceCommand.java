package ru.eddyz.tgbotdiana.commands;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.domain.CurrentListGroup;
import ru.eddyz.tgbotdiana.domain.Group;
import ru.eddyz.tgbotdiana.enums.CallbackButtonId;
import ru.eddyz.tgbotdiana.enums.CallbackButtonsText;
import ru.eddyz.tgbotdiana.enums.TypeInvoice;
import ru.eddyz.tgbotdiana.enums.TypeSubscribe;
import ru.eddyz.tgbotdiana.util.InlineKeyUtil;
import ru.eddyz.tgbotdiana.util.TelegramPaymentsHelper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendInvoiceCommand implements Command {
    private final TelegramClient telegramClient;

    private final CurrentListGroup currentListGroup;

    @Value("${telegram.bot.payment.token}")
    private String providerToken;


    @Override
    public <T extends BotApiObject> void execute(T apiObject) {
        if (apiObject instanceof CallbackQuery callbackQuery) {
            executeCallback(callbackQuery);
        }
    }

    private void executeCallback(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        var callbackId = callbackQuery.getId();

        answerCallback(callbackId);

        var splitData = data.split(":");
        var groupId = splitData[1];

        sendInvoiceMessage(callbackQuery, groupId, chatId);

    }

    private void sendInvoiceMessage(CallbackQuery callbackQuery, String groupId, Long chatId) {
        try {
            var group = currentListGroup.getGroupByTelegramId(Long.parseLong(groupId));
            var invoiceThreeMouth = telegramClient.execute(getInvoice(group.getName(),
                    group.getPriceThreeMouth(), group.getTelegramId(), TypeSubscribe.THREE_MOUTH));
            var invoiceYear = telegramClient.execute(getInvoice(group.getName(),
                    group.getPriceOneYear(), group.getTelegramId(), TypeSubscribe.ONE_YEAR));

            var keys = createKeys(group, invoiceThreeMouth, invoiceYear);
            extracted(callbackQuery.getMessage().getMessageId(), chatId, keys);
        } catch (Exception e) {
            log.error("Ошибка выполнения команды по созданию счетов: ", e);
        }
    }

    private void extracted(Integer messageId, Long chatId, InlineKeyboardMarkup keys) throws TelegramApiException {
        telegramClient.execute(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(keys)
                .parseMode(ParseMode.HTML)
                .text(generateMessage())
                .build()
        );
    }

    @NotNull
    private InlineKeyboardMarkup createKeys(Group group, String invoiceThreeMouth, String invoiceYear) {
        return new InlineKeyboardMarkup(List.of(
                new InlineKeyboardRow(
                        List.of(InlineKeyUtil.createUrlButton("Подписка на 3 месяца. %d ₽"
                                        .formatted(group.getPriceThreeMouth()),
                                invoiceThreeMouth))
                ),
                new InlineKeyboardRow(InlineKeyUtil.createUrlButton("Подписка на год. %d ₽ "
                                .formatted(group.getPriceOneYear()),
                        invoiceYear)),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .callbackData(CallbackButtonId.BACK_SELECT_GROUP_SUBSCRIBE.name())
                        .text(CallbackButtonsText.BACK.toString())
                        .build())
        ));
    }

    private CreateInvoiceLink getInvoice(String title, Integer price, Long groupId, TypeSubscribe type) throws TelegramApiException {
        try {
            return TelegramPaymentsHelper.createInvoiceLink(
                    title,
                    "Приобретение подписки: %s".formatted(title),
                    "RUB",
                    price * 100,
                    "test",
                    providerToken,
                    "%s:%s:%s".formatted(TypeInvoice.SUBSCRIBE_ON_GROUP.name(), groupId, type.name())
            );
        } catch (Exception e) {
            throw new TelegramApiException(e.getMessage());
        }
    }

    private void answerCallback(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (Exception e) {
            log.error("Ошибка ответа на кнопку, при отправке счета: {}", e.getMessage());
        }
    }

    private String generateMessage() {
        return """
                <b>Подписка</b>
                
                Чтобы войти в группу нужно приобрести подписку.
                После оплаты вам будет отправлена ссылка на вступление.
                
                ❗Возврат средств не предусмотрен.
                
                Выберите вариант подписки:
                """;
    }
}
