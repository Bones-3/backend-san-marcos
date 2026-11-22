package com.example.inventario.dto;

import java.math.BigDecimal;

public record ProductoRequest(
        String codigo, String nombre, String categoria, BigDecimal precio,
        Integer stock, Integer stockCritico, String descripcion, String imagen) {
}
