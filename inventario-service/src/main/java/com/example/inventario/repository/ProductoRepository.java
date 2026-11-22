package com.example.inventario.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.inventario.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrueOrderByIdAsc();
    long countByActivoTrue();
    long countByCategoriaIdAndActivoTrue(Long categoriaId);
}
