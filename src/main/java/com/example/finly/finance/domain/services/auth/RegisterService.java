package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.application.dtos.out.AuthenticationResponse;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.services.user.CreateUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final CreateUserService createUserService;
    private final AuthTokenService authTokenService;

    @Transactional
    public AuthenticationResponse register(UserInput input) {
        User user = createUserService.create(input);
        return authTokenService.generateAuthResponse(user);
    }
}
