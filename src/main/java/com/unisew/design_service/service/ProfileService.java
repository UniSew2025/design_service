package com.unisew.design_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="profile-service", url = "http://localhost:8085/api/v2/profile")
public interface ProfileService {

    @GetMapping("/package")
    Map<String, Object> getPackage(@RequestParam(name = "packageId") int packageId);

    @GetMapping("")
    Map<String, Object> getProfileInfo(@RequestParam(name = "accountId") int accountId);
}
