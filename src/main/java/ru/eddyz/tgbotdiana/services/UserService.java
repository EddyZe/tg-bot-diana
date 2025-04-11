package ru.eddyz.tgbotdiana.services;


import ru.eddyz.tgbotdiana.entity.User;

public interface UserService {

    User createUser(Long telegramId, String username);

    void update(User user);

    void deleteById(Long id);

    void deleteByTelegramId(Long telegramId);

    User findById(Long id);

    User findByTelegramId(Long telegramId);
}
