package ru.eddyz.tgbotdiana.commands;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.tgbotdiana.domain.CurrentListGroup;
import ru.eddyz.tgbotdiana.exception.UserException;
import ru.eddyz.tgbotdiana.services.UserService;
import ru.eddyz.tgbotdiana.util.InlineKeyUtil;
import ru.eddyz.tgbotdiana.util.ReplayKeyUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final TelegramClient telegramClient;
    private final UserService userService;

    private final CurrentListGroup currentListGroup;


    @Override
    public <T extends BotApiObject> void execute(T apiObject) {
        if (apiObject instanceof Message message) {
            executeFromMessage(message);
        }
    }

    private void executeFromMessage(Message message) {
        Long chatId = message.getChatId();
        String username = message.getFrom().getUserName();
        username = username == null ? "Username не указан" : username;

        responseOnMessage(chatId, username);
    }

    private void responseOnMessage(Long chatId, String username) {
        try {
            userService.createUser(chatId, username);
            sendMessage(chatId, generateMessage(), InlineKeyUtil.selectGroupSubscribeKey(currentListGroup.loadGroups()));
            sendMessage(chatId, generateMessageMainMenu(), ReplayKeyUtil.mainMenu());
        } catch (UserException e) {
            log.warn(e.getMessage());
            sendMessage(chatId, generateMessage(), InlineKeyUtil.selectGroupSubscribeKey(currentListGroup.loadGroups()));
            sendMessage(chatId, generateMessageMainMenu(), ReplayKeyUtil.mainMenu());
        }
    }

    private String generateMessageMainMenu() {
        return "Так же можете воспользоваться меню снизу 👇";
    }

    private String generateMessage() {
        return """
                <b>ТОПКаст</b> — твой прямой доступ к кастингам для съёмок! Подпишись на канал, и ты первым будешь получать уведомления о новых проектах: от рекламы до кино. Ничего лишнего — только актуальные кастинги, четкие условия и шанс проявить себя. Не упусти возможность попасть в мир кино и медиа!
                
                ✨ <b>Почему мы?</b>
                • <i>Только проверенные кастинги</i>
                • <i>Моментальные оповещения</i>
                • <i>Удобно, быстро, без спама</i>
                
                Подпишись сейчас — твоя роль уже ждет!
                """;
    }

    private void sendMessage(Long chatId, String message, ReplyKeyboard keyboard) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(keyboard)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в команде Start: {}", e.getMessage());
        }
    }
}
