package ru.eddyz.tgbotdiana.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.eddyz.tgbotdiana.enums.ReplayButtons;

import java.util.List;

public class ReplayKeyUtil {

    public static ReplyKeyboardMarkup mainMenu() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(
                                new KeyboardRow(
                                        ReplayButtons.BUY_SUBSCRIBE.toString(),
                                        ReplayButtons.ACTIVE_SUBSCRIBERS.toString()
                                )
                        )
                )
                .resizeKeyboard(true)
                .build();
    }

}
