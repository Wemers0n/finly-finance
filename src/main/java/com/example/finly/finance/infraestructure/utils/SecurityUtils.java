package com.example.finly.finance.infraestructure.utils;

import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.infraestructure.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    public static Optional<UserPrincipal> getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null) return Optional.empty();

        if(!(auth.getPrincipal() instanceof UserPrincipal principal)) return Optional.empty();

        return Optional.of(principal);
    }

    public static Optional<UUID> getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getId);
    }
}