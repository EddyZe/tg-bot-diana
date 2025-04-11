package ru.eddyz.tgbotdiana.util;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.eddyz.tgbotdiana.domain.Group;
import ru.eddyz.tgbotdiana.enums.CallbackButtonId;
import ru.eddyz.tgbotdiana.enums.CallbackButtonsText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InlineKeyUtil {

    public static InlineKeyboardMarkup selectGroupSubscribeKey(List<Group> group) {
        List<InlineKeyboardRow> rows = new ArrayList<>(
                group.stream()
                        .map(g -> new InlineKeyboardRow(
                                Collections.singleton(
                                        InlineKeyboardButton.builder()
                                                .callbackData("%s:%s"
                                                        .formatted(
                                                                CallbackButtonId.SELECT_GROUP_SUBSCRIBE.name(),
                                                                g.getTelegramId()
                                                        ))
                                                .text(g.getName())
                                                .build()
                                )
                        ))
                        .toList()
        );
        rows.add(new InlineKeyboardRow(
                Collections.singleton(
                        InlineKeyboardButton.builder()
                                .callbackData(CallbackButtonId.CLOSE_DEFAULT.name())
                                .text(CallbackButtonsText.CLOSE.toString())
                                .build()
                )
        ));
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardButton createUrlButton(String text, String url) {
        return InlineKeyboardButton.builder()
                .text(text)
                .url(url)
                .build();
    }
}
