package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.application.dtos.in.ForgotPasswordInput;
import com.example.finly.finance.application.dtos.in.LoginInput;
import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.application.dtos.out.ResponseOutput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.services.user.CreateUserService;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.BusinessException;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import com.example.finly.finance.infraestructure.security.TokenService;
import com.example.finly.finance.infraestructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CreateUserService createUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseOutput login(LoginInput input) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(input.email(), input.password());

        var auth = authenticationManager.authenticate(usernamePassword);

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow(UserNotExistsException::new);

        String token = tokenService.generateToken(
                principal.getId(),
                principal.getUsername(),
                principal.getFirstname(),
                principal.getLastname()
        );

        String refreshToken = tokenService.generateRefreshToken(user);

        return new ResponseOutput(token, refreshToken);
    }

    public ResponseOutput register(UserInput input) {
        User user = createUserService.create(input);

        String token = tokenService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname()
        );

        String refreshToken = tokenService.generateRefreshToken(user);

        return new ResponseOutput(token, refreshToken);
    }

    public ResponseOutput refreshToken(String refreshToken) {
        var decodedJWT = tokenService.validateRefreshToken(refreshToken);
        if (decodedJWT == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        String userId = decodedJWT.getClaim("userId").asString();
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow();

        String token = tokenService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname()
        );

        // Optional: Rotate refresh token
        tokenService.revokeRefreshTokens(user);
        String newRefreshToken = tokenService.generateRefreshToken(user);

        return new ResponseOutput(token, newRefreshToken);
    }

    public void logout(String token) {
        tokenService.blacklistToken(token);
        
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            User user = userRepository.findById(principal.getId()).orElseThrow();
            tokenService.revokeRefreshTokens(user);
        }
    }

    public void forgotPassword(ForgotPasswordInput input) {
        if (!input.newPassword().equals(input.confirmPassword())) {
            throw new BusinessException("As senhas não coincidem");
        }

        User user = userRepository.findByEmail(input.email())
                .orElseThrow(() -> new UserNotExistsException());

        user.changePassword(passwordEncoder.encode(input.newPassword()));
        userRepository.save(user);
    }
}

