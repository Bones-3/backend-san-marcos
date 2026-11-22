package com.example.inventario.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.inventario.dto.ProductoRequest;
import com.example.inventario.dto.ProductoResponse;
import com.example.inventario.exception.BadRequestException;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.model.Categoria;
import com.example.inventario.model.Inventario;
import com.example.inventario.model.Producto;
import com.example.inventario.repository.CategoriaRepository;
import com.example.inventario.repository.InventarioRepository;
import com.example.inventario.repository.ProductoRepository;
import com.example.inventario.repository.OrdenReposicionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final InventarioRepository inventarioRepository;
    private final StockService stockService;
    private final OrdenReposicionRepository ordenRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findByActivoTrueOrderByIdAsc().stream()
                .map(this::respuesta).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return respuesta(productoActivo(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        aplicarDatos(producto, request);
        producto = productoRepository.saveAndFlush(producto);
        stockService.crear(producto, request.stock() == null ? 0 : request.stock(),
                request.stockCritico() == null ? 0 : request.stockCritico());
        return respuesta(producto);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoActivo(id);
        aplicarDatos(producto, request);
        productoRepository.save(producto);
        Inventario inventario = inventarioRepository.findByProductoIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Inventario no encontrado para producto: " + id));
        inventario.setNombreProducto(producto.getNombre());
        if (request.stockCritico() != null) {
            if (request.stockCritico() < 0) throw new BadRequestException("El stock crítico no puede ser negativo");
            inventario.setStockMinimo(request.stockCritico());
        }
        inventarioRepository.save(inventario);
        if (request.stock() != null) {
            stockService.ajustar(id, request.stock() - inventario.getStockActual(), "EDICION_PRODUCTO", null);
        }
        return respuesta(producto);
    }

    @Transactional
    public void desactivar(Long id) {
        Producto producto = productoActivo(id);
        if (ordenRepository.existsByProductoIdAndEstado(id, "Pendiente")) {
            throw new BadRequestException("No se puede desactivar un producto con reposiciones pendientes");
        }
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private Producto productoActivo(Long id) {
        return productoRepository.findById(id).filter(Producto::isActivo)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + id));
    }

    private void aplicarDatos(Producto producto, ProductoRequest request) {
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(request.categoria())
                .filter(Categoria::isActiva)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + request.categoria()));
        producto.setCodigo(request.codigo());
        producto.setNombre(request.nombre());
        producto.setCategoria(categoria);
        producto.setPrecio(request.precio());
        if (request.descripcion() != null) producto.setDescripcion(request.descripcion());
        if (request.imagen() != null) producto.setImagen(request.imagen());
    }

    private ProductoResponse respuesta(Producto producto) {
        Inventario inventario = inventarioRepository.findByProductoId(producto.getId())
                .orElseThrow(() -> new NotFoundException("Inventario no encontrado para producto: " + producto.getId()));
        return new ProductoResponse(producto.getId(), producto.getCodigo(), producto.getNombre(),
                producto.getCategoria().getNombre(), producto.getPrecio(), inventario.getStockActual(),
                inventario.getStockMinimo(), producto.getDescripcion(), producto.getImagen());
    }
}
