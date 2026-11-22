package com.example.inventario.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteInventarioResponse(long productos, int unidadesTotales, BigDecimal valorInventario,
        long stockCritico, long ordenesPendientes, long unidadesRecibidas,
        List<CategoriaReporteResponse> porCategoria, List<ProductoResponse> topProductos) {
}
