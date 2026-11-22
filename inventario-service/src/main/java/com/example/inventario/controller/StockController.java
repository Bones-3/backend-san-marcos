package com.example.inventario.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.example.inventario.dto.AjusteStockRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.dto.MovimientoResponse;
import com.example.inventario.mapper.InventarioMapper;
import com.example.inventario.service.StockService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventario/producto/{productoId}")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    private final InventarioMapper inventarioMapper;

    @PatchMapping("/stock")
    public InventarioResponse ajustar(@PathVariable Long productoId, @RequestBody AjusteStockRequest request) {
        return inventarioMapper.toResponse(stockService.ajustar(productoId, request.cantidad(), request.motivo(), null));
    }

    @GetMapping("/movimientos")
    public List<MovimientoResponse> movimientos(@PathVariable Long productoId) {
        return stockService.movimientos(productoId);
    }
}
