package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.UserInput;
import com.example.finly.finance.application.dtos.UserResponse;
import com.example.finly.finance.domain.services.CreateUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserInput input){
        var userId = userService.create(input);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(userId));
    }
}
