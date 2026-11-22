package com.sanmarcos.usuario_service.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioResponse {
    private Long id;
    private String correoUsuario;
    private String correo;
    private String nombre;   
    private String apellido;
    private String apellidos;
    private String telefonoUsuario;
    private boolean estadoUsuario;
    private String run;
    private LocalDate fechaNacimiento;
    private String tipoUsuario;
    private String region;
    private String comuna;
    private String direccion;
}
