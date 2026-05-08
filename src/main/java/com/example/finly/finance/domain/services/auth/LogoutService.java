package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.infraestructure.security.AuthenticatedUserProvider;
import com.example.finly.finance.infraestructure.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticatedUserProvider userProvider;

    @Transactional
    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
        
        userProvider.getAuthenticatedPrincipal().ifPresent(principal -> {
            User user = userProvider.getAuthenticatedUser();
            refreshTokenService.revokeAllUserTokens(user);
        });
    }
}
