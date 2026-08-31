package com.officina.officina_be.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.officina.officina_be.dto.request.LoginRequest;
import com.officina.officina_be.dto.request.UserRequest;
import com.officina.officina_be.dto.response.UserResponse;
import com.officina.officina_be.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    
    //Registra utente.
    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        // TODO: sostituire con Principal/Authentication una volta introdotto JWT.
        return ResponseEntity.ok(userService.getUserByID(1L));
    }

    @PatchMapping("/users/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@RequestBody Map<String, Object> updates) {
        // TODO: sostituire con Principal/Authentication una volta introdotto JWT.
        UserResponse response = userService.updateUser(1L, updates);
        return ResponseEntity.ok(response);
    }

    //Recuperare le info di un utente tramite ID.
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserByID(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
        @PathVariable Long id, 
        @RequestBody Map<String, Object> updates
    ) {
        UserResponse response = userService.updateUser(id, updates);
        return ResponseEntity.ok(response);
    }
}
