package com.novashop.novashop_backend.dto;

import com.novashop.novashop_backend.model.Producto;

public record ProductoDto(Long id, String nombre, int precio, int stock, String atributo, String categoria,
                          int descuento, String imagen, String descripcion) {
    public static ProductoDto de(Producto p) {
        return new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), p.getAtributo(),
                p.getCategoria().getNombre(), p.getDescuento(), p.getImagen(), p.getDescripcion());
    }
}