package ru.eddyz.tgbotdiana.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Group {

    private String name;
    private Long telegramId;
    private Integer priceThreeMouth;
    private Integer priceOneYear;
    private String inviteUrl;
}
