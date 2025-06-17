package com.unisew.design_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    DRAFT("draft"),
    PENDING("pending"),
    APPROVE("approve");

    private final String value;
}
