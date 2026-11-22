package com.example.inventario.dto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id, String codigo, String nombre, String categoria, BigDecimal precio,
        int stock, int stockCritico, String descripcion, String imagen) {
}
