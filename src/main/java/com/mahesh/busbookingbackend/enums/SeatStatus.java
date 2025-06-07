package com.mahesh.busbookingbackend.enums;

import lombok.Getter;

@Getter
public enum SeatStatus {
    AVAILABLE, PENDING,BOOKED, UNAVAILABLE, SELECTED;
}
