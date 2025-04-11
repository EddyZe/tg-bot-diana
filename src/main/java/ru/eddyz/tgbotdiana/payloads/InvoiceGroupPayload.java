package ru.eddyz.tgbotdiana.payloads;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.tgbotdiana.enums.TypeSubscribe;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InvoiceGroupPayload {
    private Long telegramGroupId;
    private TypeSubscribe typeSubscribe;
}
