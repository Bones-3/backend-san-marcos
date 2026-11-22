package com.sanmarcos.usuario_service.dto;

import java.time.LocalDate;

public record RegistroRequest(
        String run,
        String nombre,
        String apellidos,
        String correo,
        String clave,
        LocalDate fechaNacimiento,
        String region,
        String comuna,
        String direccion) {
}
