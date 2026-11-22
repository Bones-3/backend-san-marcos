package com.example.inventario.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.inventario.model.MovimientoInventario;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByProductoIdOrderByFechaDesc(Long productoId);
}
