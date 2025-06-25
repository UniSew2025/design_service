package com.unisew.design_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SenderRole {


    DESIGNER("designer"),
    SCHOOL("school");

    private final String value;

}
