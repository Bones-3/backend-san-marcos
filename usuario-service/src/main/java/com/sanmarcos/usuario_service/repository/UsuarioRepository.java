package com.sanmarcos.usuario_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sanmarcos.usuario_service.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
        List<Usuario> findByEstadoUsuarioTrue();
        Optional<Usuario> findByCorreoUsuarioIgnoreCase(String correoUsuario);
        boolean existsByCorreoUsuarioIgnoreCase(String correoUsuario);
        boolean existsByRun(String run);
}
