package com.novashop.novashop_backend.dto;

import com.novashop.novashop_backend.model.Orden;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenDto(Long id, LocalDateTime fecha, String estado, int total, Long usuarioId,
                       Cliente cliente, Direccion direccion, List<Item> items) {

    public record Cliente(String nombre, String apellidos, String correo) { }

    public record Direccion(String calle, String departamento, String region, String comuna, String indicaciones) { }

    public record Item(Long id, String nombre, int precio, int cantidad) { }

    public static OrdenDto de(Orden o) {
        return new OrdenDto(
                o.getId(), o.getFecha(), o.getEstado().valor(), o.getTotal(), o.getUsuario().getId(),
                new Cliente(o.getClienteNombre(), o.getClienteApellidos(), o.getClienteCorreo()),
                new Direccion(o.getCalle(), o.getDepartamento(), o.getRegion(), o.getComuna(), o.getIndicaciones()),
                o.getItems().stream()
                        .map(i -> new Item(i.getProductoId(), i.getNombre(), i.getPrecio(), i.getCantidad()))
                        .toList());
    }
}