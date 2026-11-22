package com.nativa.usuario_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sanmarcos.usuario_service.dto.LoginRequest;
import com.sanmarcos.usuario_service.dto.RegistroRequest;
import com.sanmarcos.usuario_service.dto.UsuarioResponse;
import com.sanmarcos.usuario_service.exception.UnauthorizedException;
import com.sanmarcos.usuario_service.mapper.UsuarioMapper;
import com.sanmarcos.usuario_service.model.TipoUsuario;
import com.sanmarcos.usuario_service.model.Usuario;
import com.sanmarcos.usuario_service.repository.UsuarioRepository;
import com.sanmarcos.usuario_service.service.AuthService;
import com.sanmarcos.usuario_service.service.TokenService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UsuarioRepository usuarios;
    @Mock UsuarioMapper mapper;
    @Mock PasswordEncoder encoder;
    @Mock TokenService tokens;
    @InjectMocks AuthService auth;

    @Test
    void registrarCifraClaveYFuerzaRolCliente() {
        RegistroRequest request = new RegistroRequest(" 190110222 ", "Ana", "Rojas",
                " ANA@EXAMPLE.COM ", "secreto", null, "RM", "Santiago", "Calle 1");
        when(encoder.encode("secreto")).thenReturn("$2a$hash");
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            assertThat(usuario.getCorreoUsuario()).isEqualTo("ana@example.com");
            assertThat(usuario.getRun()).isEqualTo("190110222");
            assertThat(usuario.getTipoUsuario()).isEqualTo(TipoUsuario.CLIENTE);
            assertThat(usuario.getClaveHash()).isEqualTo("$2a$hash");
            assertThat(usuario.getEstadoUsuario()).isTrue();
            return usuario;
        });
        when(mapper.toResponse(any(Usuario.class))).thenReturn(UsuarioResponse.builder().correo("ana@example.com").build());

        UsuarioResponse response = auth.registrar(request);

        assertThat(response.getCorreo()).isEqualTo("ana@example.com");
        verify(encoder).encode("secreto");
    }

    @Test
    void loginRechazaUsuarioInactivo() {
        Usuario usuario = new Usuario();
        usuario.setCorreoUsuario("ana@example.com");
        usuario.setClaveHash("hash");
        usuario.setEstadoUsuario(false);
        when(usuarios.findByCorreoUsuarioIgnoreCase("ana@example.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> auth.login(new LoginRequest("ana@example.com", "secreto")))
                .isInstanceOf(UnauthorizedException.class);
        verify(tokens, never()).crearToken(any());
    }
}
