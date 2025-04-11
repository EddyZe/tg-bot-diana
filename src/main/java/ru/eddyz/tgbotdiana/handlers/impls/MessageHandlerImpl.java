package ru.eddyz.tgbotdiana.handlers.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.eddyz.tgbotdiana.commands.GetSubscribers;
import ru.eddyz.tgbotdiana.commands.SendInviteCommand;
import ru.eddyz.tgbotdiana.commands.ShowActiveSubscribeCommand;
import ru.eddyz.tgbotdiana.commands.StartCommand;
import ru.eddyz.tgbotdiana.enums.ReplayButtons;
import ru.eddyz.tgbotdiana.handlers.MessageHandler;



@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler {

    private final StartCommand startCommand;
    private final SendInviteCommand sendInviteCommand;
    private final GetSubscribers getSubscribers;
    private final ShowActiveSubscribeCommand showActiveSubscribeCommand;


    @Override
    public void handle(Message message) {
        if (message.hasText() && message.isUserMessage()) {
            String text = message.getText();

            if (text.equals("/start")) {
                startCommand.execute(message);
                return;
            }

            if (text.equals("/subscribers") || text.equals(ReplayButtons.BUY_SUBSCRIBE.toString())) {
                getSubscribers.execute(message);
                return;
            }

            if (text.equals("/mysubscribe") || text.equals(ReplayButtons.ACTIVE_SUBSCRIBERS.toString())) {
                showActiveSubscribeCommand.execute(message);
            }
        }

        if (message.hasSuccessfulPayment()) {
            sendInviteCommand.execute(message);
        }
    }
}
