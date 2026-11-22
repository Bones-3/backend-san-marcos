package com.sanmarcos.usuario_service.dto;

public record AuthResponse(String token, String tokenType, long expiresIn, UsuarioResponse usuario) {
}
