package com.unisew.design_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClothCategory {

    REGULAR("regular"),
    PHYSICAL_EDUCATION("pe"),;

    private final String value;
}
