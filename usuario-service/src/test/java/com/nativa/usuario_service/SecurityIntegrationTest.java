package com.nativa.usuario_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sanmarcos.usuario_service.model.TipoUsuario;
import com.sanmarcos.usuario_service.model.Usuario;
import com.sanmarcos.usuario_service.service.TokenService;

@SpringBootTest(classes = com.sanmarcos.usuario_service.UsuarioServiceApplication.class)
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired TokenService tokens;

    @Test
    void clienteNoPuedeConsultarUsuariosYAdminSi() throws Exception {
        mvc.perform(get("/usuario/usuarios/all")).andExpect(status().isUnauthorized());

        Usuario cliente = usuario(TipoUsuario.CLIENTE);
        mvc.perform(get("/usuario/usuarios/all")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.crearToken(cliente)))
                .andExpect(status().isForbidden());

        Usuario admin = usuario(TipoUsuario.ADMIN);
        mvc.perform(get("/usuario/usuarios/all")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.crearToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void registroYLoginGeneranTokenSinExponerHash() throws Exception {
        mvc.perform(post("/usuario/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"run":"190110222","nombre":"Ana","apellidos":"Rojas",
                         "correo":"cliente@example.com","clave":"secreto","region":"RM",
                         "comuna":"Santiago","direccion":"Calle 1"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoUsuario").value("Cliente"))
                .andExpect(jsonPath("$.claveHash").doesNotExist());

        mvc.perform(post("/usuario/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"correo":"cliente@example.com","clave":"secreto"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.usuario.claveHash").doesNotExist());
    }

    private Usuario usuario(TipoUsuario tipo) {
        Usuario usuario = new Usuario();
        usuario.setId(99L);
        usuario.setCorreoUsuario("test@example.com");
        usuario.setTipoUsuario(tipo);
        return usuario;
    }
}
