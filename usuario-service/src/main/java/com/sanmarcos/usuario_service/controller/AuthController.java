package com.sanmarcos.usuario_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanmarcos.usuario_service.dto.AuthResponse;
import com.sanmarcos.usuario_service.dto.LoginRequest;
import com.sanmarcos.usuario_service.dto.RegistroRequest;
import com.sanmarcos.usuario_service.dto.UsuarioResponse;
import com.sanmarcos.usuario_service.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication.getName()));
    }
}
