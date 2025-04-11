package ru.eddyz.tgbotdiana.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.tgbotdiana.exception.SubscribeNotFoundException;
import ru.eddyz.tgbotdiana.entity.Subscribe;
import ru.eddyz.tgbotdiana.entity.User;
import ru.eddyz.tgbotdiana.repositories.SubscribeRepository;
import ru.eddyz.tgbotdiana.services.SubscribeService;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscribeRepository subscribeRepository;

    @Override
    public void createNewSubscription(String name, Integer amount, String currency, Long groupTelegramId, LocalDateTime endTime, User user) {
        subscribeRepository.save(Subscribe.builder()
                .amount(amount)
                .currency(currency)
                .endDate(endTime)
                .isActive(true)
                .groupTelegramId(groupTelegramId)
                .user(user)
                .name(name)
                .startDate(LocalDateTime.now())
                .build());
    }

    @Override
    public void deleteById(Long id) {
        subscribeRepository.deleteById(id);
    }

    @Override
    public List<Subscribe> findByTelegramId(Long telegramId) {
        return subscribeRepository.findByTelegramId(telegramId);
    }

    @Override
    public Page<Subscribe> findByTelegramId(Long telegramId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return subscribeRepository.findByTelegramId(telegramId, pageable);
    }

    @Override
    public Subscribe findById(Long id) {
        return subscribeRepository.findById(id)
                .orElseThrow(() -> new SubscribeNotFoundException("Подписка с таким ID не найдена"));
    }

    @Override
    public void update(Subscribe subscribe) {
        if (subscribeRepository.findById(subscribe.getId()).isEmpty()) {
            throw new SubscribeNotFoundException("Подписка с таким ID не найдена");
        }

        subscribeRepository.save(subscribe);
    }

    @Override
    public List<Subscribe> findByTelegramIdAndGroupIdAndStatus(Long telegramId, Long groupId, Boolean status) {
        return subscribeRepository.findByGroupTelegramIdAndGroupIdAndStatus(telegramId, groupId, status);
    }

    @Override
    public List<Subscribe> findByIsActive(Boolean isActive) {
        return subscribeRepository.findByIsActive(isActive);
    }

    @Override
    public List<Subscribe> findUserIsActiveSubscribe(Long telegramId, Boolean isActive) {
        return subscribeRepository.findUserIsActiveSubscribe(telegramId, isActive);
    }
}
