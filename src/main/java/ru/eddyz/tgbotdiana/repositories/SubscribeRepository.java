package ru.eddyz.tgbotdiana.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.tgbotdiana.entity.Subscribe;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Query("select s from Subscribe s join User u on s.user.id=u.id where u.telegramId=:telegramId")
    List<Subscribe> findByTelegramId(@Param("telegramId") Long telegramId);

    @Query("select s from Subscribe s join User u on s.user.id=u.id where u.telegramId=:telegramId")
    Page<Subscribe> findByTelegramId(@Param("telegramId") Long telegramId, Pageable pageable);

    @Query("select s from Subscribe s join User u on s.user.id=u.id where u.telegramId=:telegramId and s.groupTelegramId=:groupTelegramId and s.isActive=:status")
    List<Subscribe> findByGroupTelegramIdAndGroupIdAndStatus(
            @Param("telegramId") Long userTelegramId,
            @Param("groupTelegramId") Long groupTelegramId,
            @Param("status") Boolean status);

    List<Subscribe> findByIsActive(Boolean isActive);

    @Query("select s from Subscribe s join User u on u.id=s.user.id where u.telegramId=:telegramId and s.isActive=:isActive")
    List<Subscribe> findUserIsActiveSubscribe(@Param("telegramId") Long telegramId, @Param("isActive") Boolean isActive);
}
