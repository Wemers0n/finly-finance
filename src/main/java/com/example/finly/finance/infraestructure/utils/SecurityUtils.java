package com.example.finly.finance.infraestructure.utils;

import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.infraestructure.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static UserPrincipal getCurrentUser(){

        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static UUID getCurrentUserId(){
        return getCurrentUser().getId();
    }

}