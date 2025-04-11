package ru.eddyz.tgbotdiana.commands;


import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public interface Command {
    <T extends BotApiObject> void execute(T apiObject);
}
