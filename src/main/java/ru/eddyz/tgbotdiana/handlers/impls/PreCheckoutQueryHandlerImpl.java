package ru.eddyz.tgbotdiana.handlers.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.enums.TypeInvoice;
import ru.eddyz.tgbotdiana.handlers.PreCheckoutQueryHandler;


@Slf4j
@Component
@RequiredArgsConstructor
public class PreCheckoutQueryHandlerImpl implements PreCheckoutQueryHandler {

    private final TelegramClient telegramClient;

    @Override
    public void handler(PreCheckoutQuery preCheckoutQuery) {
        try {
            var payload = preCheckoutQuery.getInvoicePayload();

            if (payload != null && payload.startsWith(TypeInvoice.SUBSCRIBE_ON_GROUP.name())) {
                telegramClient.execute(AnswerPreCheckoutQuery.builder()
                        .preCheckoutQueryId(preCheckoutQuery.getId())
                        .ok(true)
                        .build());
            } else {
                telegramClient.execute(AnswerPreCheckoutQuery.builder()
                        .preCheckoutQueryId(preCheckoutQuery.getId())
                        .ok(false)
                        .errorMessage("Пока-что мы не принимаем платежи на этот товар")
                        .build());
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка при ответе на платеж: ", e);
        }
    }
}
