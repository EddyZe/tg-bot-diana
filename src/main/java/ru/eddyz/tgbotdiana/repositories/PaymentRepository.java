package ru.eddyz.tgbotdiana.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.tgbotdiana.entity.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p join User u on u.id=p.user.id where u.telegramId=:telegramId")
    List<Payment> findByTelegramId(@Param("telegramId") Long telegramId);

    @Query("select p from Payment p join User u on u.id=p.user.id where u.telegramId=:telegramId")
    Page<Payment> findByTelegramId(@Param("telegramId") Long telegramId, Pageable  pageable);

}
