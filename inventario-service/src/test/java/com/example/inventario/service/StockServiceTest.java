package com.example.inventario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.inventario.exception.BadRequestException;
import com.example.inventario.model.Inventario;
import com.example.inventario.model.MovimientoInventario;
import com.example.inventario.model.Producto;
import com.example.inventario.repository.InventarioRepository;
import com.example.inventario.repository.MovimientoInventarioRepository;
import com.example.inventario.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    @Mock InventarioRepository inventarioRepository;
    @Mock MovimientoInventarioRepository movimientoRepository;
    @Mock ProductoRepository productoRepository;
    @InjectMocks StockService service;

    @Test
    void ajusteIncrementaStockYRegistraMovimiento() {
        Producto producto = new Producto();
        producto.setId(1001L);
        producto.setActivo(true);
        Inventario inventario = new Inventario();
        inventario.setProductoId(1001L);
        inventario.setStockActual(18);
        when(productoRepository.findById(1001L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoIdForUpdate(1001L)).thenReturn(Optional.of(inventario));

        service.ajustar(1001L, 5, "REPOSICION", null);

        assertThat(inventario.getStockActual()).isEqualTo(23);
        verify(inventarioRepository).save(inventario);
        verify(movimientoRepository).save(argThat(m -> m.getCantidad() == 5
                && m.getStockResultante() == 23 && "REPOSICION".equals(m.getMotivo())));

        assertThatThrownBy(() -> service.ajustar(1001L, -24, "AJUSTE", null))
                .isInstanceOf(BadRequestException.class);
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));
    }
}
