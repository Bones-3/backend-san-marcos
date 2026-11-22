package com.example.inventario.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.inventario.dto.OrdenReposicionRequest;
import com.example.inventario.dto.OrdenReposicionResponse;
import com.example.inventario.exception.BadRequestException;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.model.OrdenReposicion;
import com.example.inventario.model.Producto;
import com.example.inventario.repository.OrdenReposicionRepository;
import com.example.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReposicionService {
    private final OrdenReposicionRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final StockService stockService;

    @Transactional(readOnly = true)
    public List<OrdenReposicionResponse> listar() {
        return ordenRepository.findAllByOrderByFechaCreacionDesc().stream().map(this::respuesta).toList();
    }

    @Transactional
    public OrdenReposicionResponse crear(OrdenReposicionRequest request) {
        if (request.cantidad() <= 0) throw new BadRequestException("La cantidad debe ser mayor a cero");
        Producto producto = productoRepository.findById(request.productoId())
                .filter(Producto::isActivo)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + request.productoId()));
        OrdenReposicion orden = new OrdenReposicion();
        orden.setProducto(producto);
        orden.setCantidad(request.cantidad());
        orden.setEstado("Pendiente");
        orden.setFechaCreacion(LocalDateTime.now());
        return respuesta(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenReposicionResponse recibir(Long id) {
        OrdenReposicion orden = pendiente(id);
        stockService.ajustar(orden.getProducto().getId(), orden.getCantidad(), "REPOSICION", orden);
        orden.setEstado("Recibida");
        orden.setFechaRecepcion(LocalDateTime.now());
        return respuesta(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenReposicionResponse cancelar(Long id) {
        OrdenReposicion orden = pendiente(id);
        orden.setEstado("Cancelada");
        return respuesta(ordenRepository.save(orden));
    }

    private OrdenReposicion pendiente(Long id) {
        OrdenReposicion orden = ordenRepository.findWithLockById(id)
                .orElseThrow(() -> new NotFoundException("Orden de reposición no encontrada: " + id));
        if (!"Pendiente".equals(orden.getEstado())) {
            throw new BadRequestException("La orden ya no está pendiente");
        }
        return orden;
    }

    private OrdenReposicionResponse respuesta(OrdenReposicion orden) {
        return new OrdenReposicionResponse(orden.getId(), orden.getProducto().getId(),
                orden.getProducto().getNombre(), orden.getCantidad(), orden.getFechaCreacion(),
                orden.getEstado(), orden.getFechaRecepcion());
    }
}
