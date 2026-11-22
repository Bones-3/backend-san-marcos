package com.example.inventario.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.inventario.dto.CategoriaReporteResponse;
import com.example.inventario.dto.ProductoResponse;
import com.example.inventario.dto.ReporteInventarioResponse;
import com.example.inventario.dto.ResumenDashboardResponse;
import com.example.inventario.repository.OrdenReposicionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final ProductoService productoService;
    private final OrdenReposicionRepository ordenRepository;

    @Transactional(readOnly = true)
    public ResumenDashboardResponse resumen() {
        List<ProductoResponse> productos = productoService.listar();
        return new ResumenDashboardResponse(productos.size(),
                productos.stream().filter(p -> p.stock() <= p.stockCritico()).count(),
                ordenRepository.countByEstado("Pendiente"));
    }

    @Transactional(readOnly = true)
    public ReporteInventarioResponse inventario() {
        List<ProductoResponse> productos = productoService.listar();
        BigDecimal valorTotal = productos.stream().map(this::valor).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, List<ProductoResponse>> grupos = productos.stream()
                .collect(Collectors.groupingBy(ProductoResponse::categoria));
        List<CategoriaReporteResponse> categorias = grupos.entrySet().stream()
                .map(e -> new CategoriaReporteResponse(e.getKey(), e.getValue().size(),
                        e.getValue().stream().mapToInt(ProductoResponse::stock).sum(),
                        e.getValue().stream().map(this::valor).reduce(BigDecimal.ZERO, BigDecimal::add),
                        porcentaje(e.getValue(), valorTotal)))
                .sorted(Comparator.comparing(CategoriaReporteResponse::nombre)).toList();
        List<ProductoResponse> top = productos.stream()
                .sorted(Comparator.comparing(this::valor).reversed()).limit(3).toList();
        long recibidas = ordenRepository.findAll().stream()
                .filter(o -> "Recibida".equals(o.getEstado()))
                .mapToLong(o -> o.getCantidad()).sum();
        return new ReporteInventarioResponse(productos.size(),
                productos.stream().mapToInt(ProductoResponse::stock).sum(),
                valorTotal,
                productos.stream().filter(p -> p.stock() <= p.stockCritico()).count(),
                ordenRepository.countByEstado("Pendiente"), recibidas, categorias, top);
    }

    private BigDecimal valor(ProductoResponse producto) {
        return producto.precio().multiply(BigDecimal.valueOf(producto.stock()));
    }

    private int porcentaje(List<ProductoResponse> productos, BigDecimal total) {
        if (total.signum() == 0) return 0;
        BigDecimal valorCategoria = productos.stream().map(this::valor).reduce(BigDecimal.ZERO, BigDecimal::add);
        return valorCategoria.multiply(BigDecimal.valueOf(100))
                .divide(total, 0, RoundingMode.HALF_UP).intValue();
    }
}
