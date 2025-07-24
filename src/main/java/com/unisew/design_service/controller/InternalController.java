package com.unisew.design_service.controller;

import com.unisew.design_service.service.DesignRequestService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/v2/design")
@Hidden
public class InternalController {

    private final DesignRequestService designRequestService;

    @PostMapping("")
    public boolean isSafeToBan(@RequestBody List<Integer> packageIds) {
        return designRequestService.isSafeToBan(packageIds);
    }
}
