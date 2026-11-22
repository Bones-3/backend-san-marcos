package com.sanmarcos.usuario_service.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanmarcos.usuario_service.dto.AuthResponse;
import com.sanmarcos.usuario_service.dto.LoginRequest;
import com.sanmarcos.usuario_service.dto.RegistroRequest;
import com.sanmarcos.usuario_service.dto.UsuarioResponse;
import com.sanmarcos.usuario_service.exception.BadRequestException;
import com.sanmarcos.usuario_service.exception.ConflictException;
import com.sanmarcos.usuario_service.exception.ResourceNotFoundException;
import com.sanmarcos.usuario_service.exception.UnauthorizedException;
import com.sanmarcos.usuario_service.mapper.UsuarioMapper;
import com.sanmarcos.usuario_service.model.TipoUsuario;
import com.sanmarcos.usuario_service.model.Usuario;
import com.sanmarcos.usuario_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (request == null || request.correo() == null || request.clave() == null || request.run() == null) {
            throw new BadRequestException("RUN, correo y clave son obligatorios");
        }
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        String run = request.run().trim().toUpperCase(Locale.ROOT);
        if (correo.isEmpty() || run.isEmpty() || request.clave().isEmpty()) {
            throw new BadRequestException("RUN, correo y clave son obligatorios");
        }
        if (usuarioRepository.existsByCorreoUsuarioIgnoreCase(correo) || usuarioRepository.existsByRun(run)) {
            throw new ConflictException("El RUN o correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setRun(run);
        usuario.setNombres(request.nombre());
        usuario.setApellidos(request.apellidos());
        usuario.setCorreoUsuario(correo);
        usuario.setClaveHash(passwordEncoder.encode(request.clave()));
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setRegion(request.region());
        usuario.setComuna(request.comuna());
        usuario.setDireccion(request.direccion());
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setEstadoUsuario(true);
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.correo() == null || request.clave() == null) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }
        Usuario usuario = usuarioRepository.findByCorreoUsuarioIgnoreCase(request.correo().trim())
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));
        if (!Boolean.TRUE.equals(usuario.getEstadoUsuario()) || usuario.getClaveHash() == null
                || !passwordEncoder.matches(request.clave(), usuario.getClaveHash())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }
        return new AuthResponse(tokenService.crearToken(usuario), "Bearer", tokenService.getTokenSeconds(),
                usuarioMapper.toResponse(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse me(String correo) {
        Usuario usuario = usuarioRepository.findByCorreoUsuarioIgnoreCase(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (!Boolean.TRUE.equals(usuario.getEstadoUsuario())) {
            throw new UnauthorizedException("Cuenta inactiva");
        }
        return usuarioMapper.toResponse(usuario);
    }
}
