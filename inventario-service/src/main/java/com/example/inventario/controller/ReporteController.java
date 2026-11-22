package com.example.inventario.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.inventario.dto.ReporteInventarioResponse;
import com.example.inventario.dto.ResumenDashboardResponse;
import com.example.inventario.service.ReporteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReporteController {
    private final ReporteService reporteService;

    @GetMapping("/dashboard/resumen")
    public ResumenDashboardResponse resumen() {
        return reporteService.resumen();
    }

    @GetMapping("/reportes/inventario")
    public ReporteInventarioResponse inventario() {
        return reporteService.inventario();
    }
}
