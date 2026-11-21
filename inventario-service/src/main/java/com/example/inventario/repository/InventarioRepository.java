package com.example.inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.inventario.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoId(Long productoId);

    @Query("SELECT i FROM Inventario i WHERE i.stockActual <= i.stockMinimo")
    List<Inventario> findStockBajo();
}
