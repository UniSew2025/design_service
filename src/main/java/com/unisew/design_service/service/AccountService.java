package com.unisew.design_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "account-service",url = "http://localhost:8081/api/v2/account")
public interface AccountService {

    @GetMapping("")
    Map<String, Object> getAccountById(@RequestParam(name = "id") int id);
}
