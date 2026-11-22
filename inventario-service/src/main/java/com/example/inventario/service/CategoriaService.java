package com.example.inventario.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.inventario.dto.CategoriaRequest;
import com.example.inventario.dto.CategoriaResponse;
import com.example.inventario.exception.BadRequestException;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.model.Categoria;
import com.example.inventario.repository.CategoriaRepository;
import com.example.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findByActivaTrueOrderByNombreAsc().stream()
                .map(this::respuesta).toList();
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        return respuesta(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + id));
        categoria.setNombre(request.nombre());
        return respuesta(categoriaRepository.save(categoria));
    }

    @Transactional
    public void desactivar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + id));
        if (productoRepository.countByCategoriaIdAndActivoTrue(id) > 0) {
            throw new BadRequestException("La categoría contiene productos activos");
        }
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    private CategoriaResponse respuesta(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(),
                productoRepository.countByCategoriaIdAndActivoTrue(categoria.getId()));
    }
}
