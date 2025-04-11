package ru.eddyz.tgbotdiana.enums;

public enum TypeSubscribe {
    THREE_MOUTH("Подписка на 3 месяца"),
    ONE_YEAR("Подписка на год"),
    ;

    private final String type;

    TypeSubscribe(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
