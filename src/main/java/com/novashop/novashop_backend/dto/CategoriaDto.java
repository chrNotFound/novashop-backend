package com.novashop.novashop_backend.dto;

import com.novashop.novashop_backend.model.Categoria;

public record CategoriaDto(Long id, String nombre, long productos) {
    public static CategoriaDto de(Categoria c, long productos) {
        return new CategoriaDto(c.getId(), c.getNombre(), productos);
    }
}