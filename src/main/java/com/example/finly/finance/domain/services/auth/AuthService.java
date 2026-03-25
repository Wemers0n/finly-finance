package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.application.dtos.in.LoginInput;
import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.application.dtos.out.ResponseOutput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.services.user.CreateUserService;
import com.example.finly.finance.infraestructure.security.TokenService;
import com.example.finly.finance.infraestructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CreateUserService createUserService;

    public ResponseOutput login(LoginInput input) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(input.email(), input.password());

        var auth = authenticationManager.authenticate(usernamePassword);

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        String token = tokenService.generateToken(
                principal.getId(),
                principal.getUsername(),
                principal.getFirstname(),
                principal.getLastname()
        );

        return new ResponseOutput(token);
    }

    public ResponseOutput register(UserInput input) {
        User user = createUserService.create(input);

        String token = tokenService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname()
        );
        return new ResponseOutput(token);
    }
}

