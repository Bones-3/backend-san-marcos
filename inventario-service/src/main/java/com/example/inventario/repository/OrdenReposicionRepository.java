package com.example.inventario.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import com.example.inventario.model.OrdenReposicion;

public interface OrdenReposicionRepository extends JpaRepository<OrdenReposicion, Long> {
    List<OrdenReposicion> findAllByOrderByFechaCreacionDesc();
    long countByEstado(String estado);
    boolean existsByProductoIdAndEstado(Long productoId, String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrdenReposicion o WHERE o.id = :id")
    Optional<OrdenReposicion> findWithLockById(@Param("id") Long id);
}
