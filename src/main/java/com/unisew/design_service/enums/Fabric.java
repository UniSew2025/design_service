package com.unisew.design_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Fabric {
    COTTON("Cotton"),
    POLYESTER("Polyester"),
    CVC("CVC (Chief Value Cotton)"),
    TC("TC (Tetron Cotton)"),
    VISCOSE("Viscose"),
    BAMBOO("Bamboo"),
    OXFORD("Oxford"),
    KHAKI("Khaki"),
    JEAN("Jean"),
    SILK("Silk"),
    FLEECE("Fleece"),
    LANA("Lana"),
    WOOL("Wool"),
    DENIM("Denim"),
    JERSEY("Jersey"),
    SPANDEX("Spandex"),
    MODAL("Modal"),
    MICROFIBER("Microfiber"),
    POLYESTER_COOL("Cool Polyester"),
    POPLIN("Poplin");

    private final String value;
}

