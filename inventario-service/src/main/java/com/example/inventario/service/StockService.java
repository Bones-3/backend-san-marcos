package com.example.inventario.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.inventario.dto.MovimientoResponse;
import com.example.inventario.exception.BadRequestException;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.model.Inventario;
import com.example.inventario.model.MovimientoInventario;
import com.example.inventario.model.OrdenReposicion;
import com.example.inventario.model.Producto;
import com.example.inventario.repository.InventarioRepository;
import com.example.inventario.repository.MovimientoInventarioRepository;
import com.example.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Inventario crear(Producto producto, int stock, int minimo) {
        if (stock < 0 || minimo < 0) throw new BadRequestException("El stock no puede ser negativo");
        Inventario inventario = new Inventario(null, producto.getId(), producto.getNombre(),
                stock, minimo, "unidad", LocalDateTime.now());
        inventario = inventarioRepository.save(inventario);
        if (stock != 0) registrar(producto, stock, stock, "ALTA", null);
        return inventario;
    }

    @Transactional
    public Inventario ajustar(Long productoId, int cantidad, String motivo, OrdenReposicion orden) {
        Producto producto = productoRepository.findById(productoId)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + productoId));
        Inventario inventario = inventarioRepository.findByProductoIdForUpdate(productoId)
                .orElseThrow(() -> new NotFoundException("Inventario no encontrado para producto: " + productoId));
        long nuevoStock = (long) inventario.getStockActual() + cantidad;
        if (nuevoStock < 0 || nuevoStock > Integer.MAX_VALUE) {
            throw new BadRequestException("El ajuste deja un stock fuera de rango");
        }
        inventario.setStockActual((int) nuevoStock);
        inventario.setUltimaActualizacion(LocalDateTime.now());
        inventarioRepository.save(inventario);
        if (cantidad != 0) registrar(producto, cantidad, (int) nuevoStock,
                motivo == null || motivo.isBlank() ? "AJUSTE" : motivo, orden);
        return inventario;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> movimientos(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId).stream()
                .map(m -> new MovimientoResponse(m.getId(), m.getProducto().getId(), m.getCantidad(),
                        m.getStockResultante(), m.getMotivo(), m.getFecha(),
                        m.getOrdenReposicion() == null ? null : m.getOrdenReposicion().getId()))
                .toList();
    }

    private void registrar(Producto producto, int cantidad, int stockResultante, String motivo, OrdenReposicion orden) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setStockResultante(stockResultante);
        movimiento.setMotivo(motivo);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setOrdenReposicion(orden);
        movimientoRepository.save(movimiento);
    }
}
