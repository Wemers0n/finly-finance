package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.application.dtos.in.ForgotPasswordInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    @Transactional
    public void forgotPassword(ForgotPasswordInput input) {
        passwordValidator.validate(input.newPassword(), input.confirmPassword());

        User user = userRepository.findByEmail(input.email())
                .orElseThrow(UserNotExistsException::new);

        user.changePassword(passwordEncoder.encode(input.newPassword()));
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
