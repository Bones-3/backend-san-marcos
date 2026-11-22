package com.sanmarcos.usuario_service.dto;

import java.time.LocalDate;

import com.sanmarcos.usuario_service.model.TipoUsuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String correoUsuario;

    private String nombres;
    
    private String apellidos;

    private String telefonoUsuario;
    private String clave;

    private String run;
    private LocalDate fechaNacimiento;
    private TipoUsuario tipoUsuario;
    private String region;
    private String comuna;
    private String direccion;
}
