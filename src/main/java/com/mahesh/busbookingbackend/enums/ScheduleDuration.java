package com.mahesh.busbookingbackend.enums;

public enum ScheduleDuration {
    ONE_MONTH(1),
    TWO_MONTHS(2),
    THREE_MONTHS(3),
    FOUR_MONTHS(4);

    private final int months;

    ScheduleDuration(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }
}
