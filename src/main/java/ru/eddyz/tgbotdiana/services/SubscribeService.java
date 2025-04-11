package ru.eddyz.tgbotdiana.services;


import org.springframework.data.domain.Page;
import ru.eddyz.tgbotdiana.entity.Subscribe;
import ru.eddyz.tgbotdiana.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscribeService {

    void createNewSubscription(String name, Integer amount, String currency, Long groupTelegramId, LocalDateTime endTime, User user);

    void deleteById(Long id);

    List<Subscribe> findByTelegramId(Long telegramId);

    Page<Subscribe> findByTelegramId(Long telegramId, Integer page, Integer limit);

    Subscribe findById(Long id);

    void update(Subscribe subscribe);

    List<Subscribe> findByTelegramIdAndGroupIdAndStatus(Long telegramId, Long groupId, Boolean status);

    List<Subscribe> findByIsActive(Boolean isActive);

    List<Subscribe> findUserIsActiveSubscribe(Long telegramId, Boolean isActive);

}
