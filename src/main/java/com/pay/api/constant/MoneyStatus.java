package com.pay.api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoneyStatus {
    받기완료("T"),
    받기미완료("F");

    private String status;
}
