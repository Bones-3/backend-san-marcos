package com.example.inventario.dto;

import java.math.BigDecimal;

public record CategoriaReporteResponse(String nombre, long productos, int unidades,
        BigDecimal valor, int porcentaje) {
}
