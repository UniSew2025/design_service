package com.unisew.design_service.utils;

import com.unisew.design_service.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

public class AccessCurrentLoginUser {

    public static Integer getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Integer.valueOf(userDetails.getUsername());
        }
        return null;
    }

    public static Map<String, Object> getAccount(AccountService accountService) {
        Integer id = getId();
        if (id == null) {
            return null;
        }
        return accountService.getAccountById(getId());
    }

    public static String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority().replace("ROLE_", "").toLowerCase())
                    .orElse(null);
        }
        return null;
    }
}
