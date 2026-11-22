package com.example.inventario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.inventario.dto.OrdenReposicionRequest;
import com.example.inventario.exception.BadRequestException;
import com.example.inventario.model.OrdenReposicion;
import com.example.inventario.model.Producto;
import com.example.inventario.repository.OrdenReposicionRepository;
import com.example.inventario.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ReposicionServiceTest {
    @Mock OrdenReposicionRepository ordenRepository;
    @Mock ProductoRepository productoRepository;
    @Mock StockService stockService;
    @InjectMocks ReposicionService service;

    @Test
    void recibirSoloSumaStockUnaVez() {
        Producto producto = new Producto();
        producto.setId(1001L);
        producto.setNombre("Alimento");
        producto.setActivo(true);
        OrdenReposicion orden = new OrdenReposicion();
        orden.setId(7L);
        orden.setProducto(producto);
        orden.setCantidad(5);
        orden.setEstado("Pendiente");
        when(productoRepository.findById(1001L)).thenReturn(Optional.of(producto));
        when(ordenRepository.save(any(OrdenReposicion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ordenRepository.findWithLockById(7L)).thenReturn(Optional.of(orden));

        var creada = service.crear(new OrdenReposicionRequest(1001L, 5));
        assertThat(creada.estado()).isEqualTo("Pendiente");

        var recibida = service.recibir(7L);
        assertThat(recibida.estado()).isEqualTo("Recibida");
        assertThat(recibida.fechaRecepcion()).isNotNull();
        verify(stockService, times(1)).ajustar(1001L, 5, "REPOSICION", orden);

        assertThatThrownBy(() -> service.recibir(7L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("pendiente");
        verifyNoMoreInteractions(stockService);
    }
}
