package com.sanmarcos.usuario_service.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sanmarcos.usuario_service.model.TipoUsuario;
import com.sanmarcos.usuario_service.model.Usuario;
import com.sanmarcos.usuario_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.auth.admin-email:}")
    private String adminEmail;

    @Value("${app.auth.admin-password:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() && adminPassword.isBlank()) {
            return;
        }
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException("ADMIN_EMAIL y ADMIN_PASSWORD deben configurarse juntos");
        }

        String correo = adminEmail.trim().toLowerCase(Locale.ROOT);
        var existente = usuarioRepository.findByCorreoUsuarioIgnoreCase(correo);
        if (existente.isPresent()) {
            if (existente.get().getTipoUsuario() != TipoUsuario.ADMIN) {
                throw new IllegalStateException("ADMIN_EMAIL coincide con una cuenta que no es administradora");
            }
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setCorreoUsuario(correo);
        usuario.setClaveHash(passwordEncoder.encode(adminPassword));
        usuario.setNombres("Administrador");
        usuario.setApellidos("San Marcos");
        usuario.setTipoUsuario(TipoUsuario.ADMIN);
        usuario.setEstadoUsuario(true);
        usuarioRepository.save(usuario);
    }
}
