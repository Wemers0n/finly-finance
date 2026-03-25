package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.application.dtos.out.UserOutput;
import com.example.finly.finance.domain.services.user.CreateUserService;
import com.example.finly.finance.infraestructure.security.UserPrincipal;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserService userService;

    @Operation(summary = "Criar Usuário", description = "")
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserInput input){
        UUID userId = userService.create(input).getId();
        URI locationUserUri = UriUtils.buildLocationUri(userId);

        return ResponseEntity.created(locationUserUri).build();
    }

    @GetMapping("/me")
    public UserOutput getUser(@AuthenticationPrincipal UserPrincipal principal){
        return new UserOutput(
                principal.getId(),
                principal.getFirstname(),
                principal.getLastname(),
                principal.getUsername()
        );
    }
}
