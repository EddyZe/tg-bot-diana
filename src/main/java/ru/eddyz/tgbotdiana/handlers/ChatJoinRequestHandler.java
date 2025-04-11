package ru.eddyz.tgbotdiana.handlers;

import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;

public interface ChatJoinRequestHandler {
    void handle(ChatJoinRequest chatJoinRequest);
}
