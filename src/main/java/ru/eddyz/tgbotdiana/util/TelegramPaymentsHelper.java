package ru.eddyz.tgbotdiana.util;


import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

public class TelegramPaymentsHelper {

    public static CreateInvoiceLink createInvoiceLink(String title,
                                                      String description,
                                                      String currency,
                                                      Integer amount,
                                                      String providerDataJson,
                                                      String providerToken,
                                                      String payload) {
        return CreateInvoiceLink.builder()
                .currency(currency)
                .description(description)
                .providerToken(providerToken == null ? "" : providerToken)
//                .providerData(providerDataJson == null ? "" : providerDataJson)
                .title(title)
                .payload(payload)
                .price(new LabeledPrice(title, amount))
                .build();

    }
}
