package ru.eddyz.tgbotdiana.services;

import org.springframework.data.domain.Page;
import ru.eddyz.tgbotdiana.entity.Payment;
import ru.eddyz.tgbotdiana.entity.User;

import java.util.List;

public interface PaymentService {
    void save(Integer amount, String currency, String description, User user);

    List<Payment> findByTelegramId(Long telegramId);

    Page<Payment> findByTelegramId(Long telegramId, Integer page, Integer limit);

    void deleteById(Long id);

    Payment findById(Long id);
}
