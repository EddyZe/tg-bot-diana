package ru.eddyz.tgbotdiana.enums;

public enum ReplayButtons {
    BUY_SUBSCRIBE("Купить подписку 💵"),
    ACTIVE_SUBSCRIBERS("Мои подписки 📃");
    private final String btn;

    ReplayButtons(String btn) {
        this.btn = btn;
    }

    public String toString() {
        return btn;
    }
}
