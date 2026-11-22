package com.sanmarcos.usuario_service.mapper;

import org.springframework.stereotype.Component;

import com.sanmarcos.usuario_service.dto.UsuarioRequest;
import com.sanmarcos.usuario_service.dto.UsuarioResponse;
import com.sanmarcos.usuario_service.model.Usuario;

@Component
public class UsuarioMapper {
    public Usuario toEntity(UsuarioRequest request) {

        Usuario usuario = new Usuario();

        usuario.setCorreoUsuario(request.getCorreoUsuario());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setTelefonoUsuario(request.getTelefonoUsuario());
        usuario.setEstadoUsuario(true);
        usuario.setRun(request.getRun());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getTipoUsuario() != null) {
            usuario.setTipoUsuario(request.getTipoUsuario());
        }
        usuario.setRegion(request.getRegion());
        usuario.setComuna(request.getComuna());
        usuario.setDireccion(request.getDireccion());

        return usuario;   
    }
    
    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .correoUsuario(usuario.getCorreoUsuario())
                .correo(usuario.getCorreoUsuario())
                .nombre(usuario.getNombres())
                .apellido(usuario.getApellidos())
                .apellidos(usuario.getApellidos())
                .telefonoUsuario(usuario.getTelefonoUsuario())
                .estadoUsuario(Boolean.TRUE.equals(usuario.getEstadoUsuario()))
                .run(usuario.getRun())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .tipoUsuario(usuario.getTipoUsuario() == null ? null : switch (usuario.getTipoUsuario()) {
                    case ADMIN -> "Administrador";
                    case VENDEDOR -> "Vendedor";
                    case CLIENTE -> "Cliente";
                })
                .region(usuario.getRegion())
                .comuna(usuario.getComuna())
                .direccion(usuario.getDireccion())
                .build();
    }
}
