package ru.eddyz.tgbotdiana.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class CurrentListGroup {

    @Value("${telegram.bot.channel.childlike.id}")
    private Long childlikeGroupId;

    @Value("${telegram.bot.channel.childlike.name}")
    private String childlikeGroupName;

    @Value("${telegram.bot.channel.childlike.price.default}")
    private Integer childlikeGroupPriceDefault;

    @Value("${telegram.bot.channel.childlike.price.year}")
    private Integer childlikeGroupPriceYear;

    @Value("${telegram.bot.channel.childlike.price.invateurl}")
    private String childlikeGroupInviteUrl;

    @Value("${telegram.bot.channel.adult.id}")
    private Long adultGroupId;

    @Value("${telegram.bot.channel.adult.name}")
    private String adultGroupName;

    @Value("${telegram.bot.channel.adult.price.default}")
    private Integer adultGroupPriceDefault;

    @Value("${telegram.bot.channel.adult.price.year}")
    private Integer adultGroupPriceYear;

    @Value("${telegram.bot.channel.adult.price.invateurl}")
    private String adultGroupInviteUrl;
    public List<Group> loadGroups() {
        return List.of(
                new Group(childlikeGroupName, childlikeGroupId, childlikeGroupPriceDefault, childlikeGroupPriceYear,childlikeGroupInviteUrl),
                new Group(adultGroupName, adultGroupId, adultGroupPriceDefault, adultGroupPriceYear, adultGroupInviteUrl)
        );
    }

    public Group getGroupByTelegramId(Long groupId) {
        return loadGroups()
                .stream()
                .filter(g -> g.getTelegramId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Группа с таким ID не найдена"));
    }
}
