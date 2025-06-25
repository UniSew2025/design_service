package com.unisew.design_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    CREATED("created"),
    PAID("paid"),
    DESIGNING("designing"),
    COMPLETED("completed");

    private final String value;
}
