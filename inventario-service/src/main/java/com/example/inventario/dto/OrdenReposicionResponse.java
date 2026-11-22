package com.example.inventario.dto;

import java.time.LocalDateTime;

public record OrdenReposicionResponse(Long id, Long productoId, String nombre, int cantidad,
        LocalDateTime fecha, String estado, LocalDateTime fechaRecepcion) {
}
