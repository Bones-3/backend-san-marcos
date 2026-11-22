package com.example.inventario.dto;

import java.time.LocalDateTime;

public record MovimientoResponse(Long id, Long productoId, int cantidad, int stockResultante,
        String motivo, LocalDateTime fecha, Long ordenReposicionId) {
}
