package ru.eddyz.tgbotdiana.handlers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.eddyz.tgbotdiana.commands.CloseDefaultCommand;
import ru.eddyz.tgbotdiana.commands.GetSubscribers;
import ru.eddyz.tgbotdiana.commands.SendInvoiceCommand;
import ru.eddyz.tgbotdiana.enums.CallbackButtonId;
import ru.eddyz.tgbotdiana.handlers.CallBackHandler;


@Component
@RequiredArgsConstructor
public class CallBackHandlerImpl implements CallBackHandler {

    private final SendInvoiceCommand sendInvoiceCommand;
    private final GetSubscribers getSubscribers;
    private final CloseDefaultCommand closeDefaultCommand;


    @Override
    public void handle(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData() == null ? "" : callbackQuery.getData();

        if (data.startsWith(CallbackButtonId.SELECT_GROUP_SUBSCRIBE.name())) {
            sendInvoiceCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(CallbackButtonId.BACK_SELECT_GROUP_SUBSCRIBE.name())) {
            getSubscribers.execute(callbackQuery);
            return;
        }

        if (data.equals(CallbackButtonId.CLOSE_DEFAULT.name())) {
            closeDefaultCommand.execute(callbackQuery);
        }
    }
}
