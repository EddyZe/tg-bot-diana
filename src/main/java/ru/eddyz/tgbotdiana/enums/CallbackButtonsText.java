package ru.eddyz.tgbotdiana.enums;




public enum CallbackButtonsText {
    BACK("Назад ⏪"),
    CLOSE("Закрыть ❌");

    private final String btn;

    CallbackButtonsText(String btn) {
        this.btn = btn;
    }

    public String toString() {
        return btn;
    }
}
