package ru.eddyz.tgbotdiana.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eddyz.tgbotdiana.exception.UserException;
import ru.eddyz.tgbotdiana.exception.UserNotFoundException;
import ru.eddyz.tgbotdiana.entity.User;
import ru.eddyz.tgbotdiana.repositories.UserRepository;
import ru.eddyz.tgbotdiana.services.UserService;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(Long telegramId, String username) {
        if (userRepository.findByTelegramId(telegramId).isPresent()) {
            throw new UserException("Пользователь с таким Telegram ID уже существует");
        }

        return userRepository.save(User.builder()
                .username(username)
                .telegramId(telegramId)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public void update(User user) {
        if (userRepository.findById(user.getId()).isEmpty())
            throw new UserNotFoundException("Такой пользователь не найден!");

        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteByTelegramId(Long telegramId) {
        userRepository.deleteByTelegramId(telegramId);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким ID не найден"));
    }

    @Override
    public User findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким Telegram ID не найден"));
    }
}
