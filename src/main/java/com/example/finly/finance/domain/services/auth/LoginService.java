package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.application.dtos.in.LoginInput;
import com.example.finly.finance.application.dtos.out.AuthenticationResponse;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import com.example.finly.finance.infraestructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final AuthTokenService authTokenService;
    private final UserRepository userRepository;

    public AuthenticationResponse login(LoginInput input) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(input.email(), input.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow(UserNotExistsException::new);

        return authTokenService.generateAuthResponse(user);
    }
}
