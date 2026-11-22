package com.example.inventario.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.exception.ConflictException;
import com.example.inventario.mapper.InventarioMapper;
import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import com.example.inventario.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;
    private final ProductoRepository productoRepository;

    @Transactional
    public InventarioResponse crearInventario(InventarioRequest request) {
        log.info("Iniciando creación de inventario para producto: {}", request.getProductoId());

        if (productoRepository.existsById(request.getProductoId())) {
            throw new ConflictException("Use la API de productos para crear inventario del catálogo");
        }
        if (inventarioRepository.findByProductoId(request.getProductoId()).isPresent()) {
            throw new ConflictException("El producto ya tiene inventario");
        }

        Inventario inventario = inventarioMapper.toEntity(request);

        Inventario guardado = inventarioRepository.save(inventario);
        log.info("Inventario creado para producto: {}", request.getProductoId());
        return inventarioMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public InventarioResponse obtenerPorId(Long id) {
        log.info("Buscando inventario con ID: {}", id);
        Inventario inventario = inventarioRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("No se encontró el inventario con ID: {}", id);
                return new NotFoundException("Inventario no encontrado con id: " + id);
            });
        return inventarioMapper.toResponse(inventario);
    }

    @Transactional(readOnly = true)
    public InventarioResponse obtenerPorProductoId(Long productoId) {
        log.info("Buscando inventario del producto: {}", productoId);
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
            .orElseThrow(() -> {
                log.warn("No se encontró inventario para el producto: {}", productoId);
                return new NotFoundException("Inventario no encontrado para producto: " + productoId);
            });
        return inventarioMapper.toResponse(inventario);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerTodos() {
        log.info("Solicitando la lista de todos los inventarios");
        return inventarioRepository.findAll()
            .stream()
            .map(inventarioMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public InventarioResponse actualizar(Long id, InventarioRequest request) {
        log.info("Iniciando actualización del inventario con ID: {}", id);

        Inventario inventario = inventarioRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Error al actualizar: Inventario con ID {} no existe", id);
                return new NotFoundException("Inventario no encontrado con id: " + id);
            });

        if (productoRepository.existsById(inventario.getProductoId())) {
            throw new ConflictException("Use la API de productos o ajustes de stock para este inventario");
        }
        if (productoRepository.existsById(request.getProductoId())) {
            throw new ConflictException("No se puede vincular inventario legado a un producto del catálogo");
        }

        inventario.setProductoId(request.getProductoId());
        inventario.setNombreProducto(request.getNombreProducto());
        inventario.setStockActual(request.getStockActual());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setUnidadMedida(request.getUnidadMedida());
        inventario.setUltimaActualizacion(LocalDateTime.now());

        Inventario actualizado = inventarioRepository.save(inventario);
        log.info("Inventario actualizado con id: {}", id);
        return inventarioMapper.toResponse(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Iniciando eliminación del inventario con ID: {}", id);
        if (!inventarioRepository.existsById(id)) {
            log.error("Error al eliminar: Inventario con ID {} no existe", id);
            throw new NotFoundException("Inventario no encontrado con id: " + id);
        }
        Inventario inventario = inventarioRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Inventario no encontrado con id: " + id));
        if (productoRepository.existsById(inventario.getProductoId())) {
            throw new ConflictException("No se puede eliminar el inventario de un producto del catálogo");
        }
        inventarioRepository.deleteById(id);
        log.info("Inventario eliminado con id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerStockBajo() {
        log.info("Solicitando la lista de inventarios con stock bajo");
        return inventarioRepository.findStockBajo()
            .stream()
            .map(inventarioMapper::toResponse)
            .collect(Collectors.toList());
    }
}
