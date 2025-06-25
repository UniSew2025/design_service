package com.unisew.design_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    CREATED("created"),// vua tao ra
    UNPAID("unpaid"), // da chon package chua thanh toan
    PAID("paid"), // da thanh toan package
    DESIGNING("designing"), // dang duoc thiet ke
    COMPLETED("completed"); //da hoan thanh

    private final String value;
}
