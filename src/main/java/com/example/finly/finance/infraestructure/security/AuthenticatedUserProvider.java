package com.example.finly.finance.infraestructure.security;

import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    public Optional<UserPrincipal> getAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public User getAuthenticatedUser() {
        return getAuthenticatedPrincipal()
                .flatMap(principal -> userRepository.findById(principal.getId()))
                .orElseThrow(UserNotExistsException::new);
    }
}
