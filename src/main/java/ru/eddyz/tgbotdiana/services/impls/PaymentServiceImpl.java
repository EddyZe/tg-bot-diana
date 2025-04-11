package ru.eddyz.tgbotdiana.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.tgbotdiana.exception.PaymentNotFoundException;
import ru.eddyz.tgbotdiana.entity.Payment;
import ru.eddyz.tgbotdiana.entity.User;
import ru.eddyz.tgbotdiana.repositories.PaymentRepository;
import ru.eddyz.tgbotdiana.services.PaymentService;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;


    @Override
    public void save(Integer amount, String currency, String description, User user) {
        paymentRepository.save(Payment.builder()
                .amount(amount)
                .currency(currency)
                .description(description)
                .date(LocalDateTime.now())
                .user(user)
                .build());
    }

    @Override
    public List<Payment> findByTelegramId(Long telegramId) {
        return paymentRepository.findByTelegramId(telegramId);
    }

    @Override
    public Page<Payment> findByTelegramId(Long telegramId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return paymentRepository.findByTelegramId(telegramId, pageable);
    }

    @Override
    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Платеж с таким ID не найден"));
    }
}
