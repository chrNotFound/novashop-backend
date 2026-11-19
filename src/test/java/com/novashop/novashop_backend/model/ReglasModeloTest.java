package com.novashop.novashop_backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReglasModeloTest {

    @Test
    @DisplayName("RM-01 Precio final con descuento redondeado")
    void precioConDescuento() {
        Producto p = new Producto();
        p.setPrecio(99990);
        p.setDescuento(15);
        assertEquals(84992, p.precioFinal());
    }

    @Test
    @DisplayName("RM-02 Sin descuento el precio final es el original")
    void precioSinDescuento() {
        Producto p = new Producto();
        p.setPrecio(29990);
        p.setDescuento(0);
        assertEquals(29990, p.precioFinal());
    }

    @Test
    @DisplayName("RM-03 Solo pagada y enviada descuentan stock")
    void estadosQueDescuentanStock() {
        assertTrue(EstadoOrden.PAGADA.descuentaStock());
        assertTrue(EstadoOrden.ENVIADA.descuentaStock());
        assertFalse(EstadoOrden.RECHAZADA.descuentaStock());
        assertFalse(EstadoOrden.CANCELADA.descuentaStock());
    }

    @Test
    @DisplayName("RM-04 Conversión de textos a estado y rol")
    void conversionDeTextos() {
        assertEquals(EstadoOrden.ENVIADA, EstadoOrden.desde(" Enviada "));
        assertEquals(Rol.ADMIN, Rol.desde("admin"));
        assertEquals(Rol.CLIENTE, Rol.desde(null));
        assertThrows(IllegalArgumentException.class, () -> Rol.desde("superusuario"));
    }
}
