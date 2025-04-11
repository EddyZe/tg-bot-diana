package ru.eddyz.tgbotdiana.core;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Group {

    private final String url;
    private final Long telegramId;
}
