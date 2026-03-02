package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.domain.services.user.CreateUserService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserService userService;

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserInput input){
        UUID userId = userService.create(input);
        URI locationUserUri = UriUtils.buildLocationUri(userId);

        return ResponseEntity.created(locationUserUri).build();
    }
}
