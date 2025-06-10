package com.unisew.design_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClothType {

    PANTS("pants"),
    SHIRT("shirt"),
    SKIRT("skirt");

    private final String value;
}
