package ru.eddyz.tgbotdiana.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscribe")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Subscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean isActive;

    private Integer amount;

    private String currency;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long groupTelegramId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
