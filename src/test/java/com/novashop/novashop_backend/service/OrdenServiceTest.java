package com.novashop.novashop_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.novashop.novashop_backend.dto.OrdenDto;
import com.novashop.novashop_backend.dto.OrdenRequest;
import com.novashop.novashop_backend.exception.ReglaNegocioException;
import com.novashop.novashop_backend.model.EstadoOrden;
import com.novashop.novashop_backend.model.Orden;
import com.novashop.novashop_backend.model.OrdenItem;
import com.novashop.novashop_backend.model.Producto;
import com.novashop.novashop_backend.model.Rol;
import com.novashop.novashop_backend.model.Usuario;
import com.novashop.novashop_backend.repository.OrdenRepository;
import com.novashop.novashop_backend.repository.ProductoRepository;
import com.novashop.novashop_backend.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenes;
    @Mock
    private ProductoRepository productos;
    @Mock
    private UsuarioRepository usuarios;

    @InjectMocks
    private OrdenService service;

    private Usuario cliente;
    private Producto audifonos;

    @BeforeEach
    void preparar() {
        cliente = new Usuario();
        cliente.setId(2L);
        cliente.setNombre("Cliente");
        cliente.setApellidos("Demo");
        cliente.setCorreo("cliente@novashop.cl");
        cliente.setRol(Rol.CLIENTE);

        audifonos = new Producto();
        audifonos.setId(1L);
        audifonos.setNombre("Audifonos");
        audifonos.setPrecio(10000);
        audifonos.setDescuento(10);
        audifonos.setStock(5);
    }

    private OrdenRequest pedido(int cantidad, String resultadoPago) {
        return new OrdenRequest(
                new OrdenRequest.Cliente("Cliente", "Demo", "cliente@novashop.cl"),
                new OrdenRequest.Direccion("Calle 123", "", "Valparaíso", "Viña del Mar", ""),
                List.of(new OrdenRequest.Item(1L, cantidad)),
                resultadoPago);
    }

    @Test
    @DisplayName("OS-01 Pago aprobado: orden pagada, total con descuento y stock descontado")
    void crearConPagoAprobado() {
        when(usuarios.findByCorreoIgnoreCase("cliente@novashop.cl")).thenReturn(Optional.of(cliente));
        when(productos.findById(1L)).thenReturn(Optional.of(audifonos));
        when(ordenes.save(any(Orden.class))).thenAnswer(i -> i.getArgument(0));

        OrdenDto orden = service.crear(pedido(2, "aprobado"), "cliente@novashop.cl");

        assertEquals("pagada", orden.estado());
        assertEquals(18000, orden.total()); // 2 x (10000 - 10%)
        assertEquals(3, audifonos.getStock());
        verify(productos).save(audifonos);
    }

    @Test
    @DisplayName("OS-02 Pago rechazado: orden rechazada y el stock no cambia")
    void crearConPagoRechazado() {
        when(usuarios.findByCorreoIgnoreCase("cliente@novashop.cl")).thenReturn(Optional.of(cliente));
        when(productos.findById(1L)).thenReturn(Optional.of(audifonos));
        when(ordenes.save(any(Orden.class))).thenAnswer(i -> i.getArgument(0));

        OrdenDto orden = service.crear(pedido(2, "rechazado"), "cliente@novashop.cl");

        assertEquals("rechazada", orden.estado());
        assertEquals(5, audifonos.getStock());
        verify(productos, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("OS-03 Stock insuficiente: no se crea la orden")
    void crearSinStockSuficiente() {
        when(usuarios.findByCorreoIgnoreCase("cliente@novashop.cl")).thenReturn(Optional.of(cliente));
        when(productos.findById(1L)).thenReturn(Optional.of(audifonos));

        assertThrows(ReglaNegocioException.class,
                () -> service.crear(pedido(10, "aprobado"), "cliente@novashop.cl"));
        verify(ordenes, never()).save(any(Orden.class));
    }

    @Test
    @DisplayName("OS-04 Cancelar una orden pagada repone el stock")
    void cancelarReponeStock() {
        Orden orden = new Orden();
        orden.setId(7L);
        orden.setUsuario(cliente);
        orden.setEstado(EstadoOrden.PAGADA);
        OrdenItem item = new OrdenItem();
        item.setProductoId(1L);
        item.setNombre("Audifonos");
        item.setPrecio(9000);
        item.setCantidad(2);
        orden.agregarItem(item);

        when(ordenes.findById(7L)).thenReturn(Optional.of(orden));
        when(productos.findById(1L)).thenReturn(Optional.of(audifonos));
        when(ordenes.save(any(Orden.class))).thenAnswer(i -> i.getArgument(0));

        OrdenDto resultado = service.cambiarEstado(7L, "cancelada");

        assertEquals("cancelada", resultado.estado());
        assertEquals(7, audifonos.getStock());
    }

    @Test
    @DisplayName("OS-05 Un cliente no puede ver la orden de otro usuario")
    void obtenerOrdenAjena() {
        Orden orden = new Orden();
        orden.setId(8L);
        orden.setUsuario(cliente);
        orden.setEstado(EstadoOrden.PAGADA);
        when(ordenes.findById(8L)).thenReturn(Optional.of(orden));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> service.obtener(8L, "otro@novashop.cl", false));
        assertEquals(HttpStatus.FORBIDDEN, error.getEstado());
    }

    @Test
    @DisplayName("OS-06 Estado no permitido lanza error")
    void estadoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> service.cambiarEstado(7L, "perdida"));
    }
}
