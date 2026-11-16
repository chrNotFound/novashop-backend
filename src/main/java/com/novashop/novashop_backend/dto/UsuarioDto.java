package com.novashop.novashop_backend.dto;

import com.novashop.novashop_backend.model.Usuario;

public record UsuarioDto(Long id, String nombre, String apellidos, String correo, String rol) {
    public static UsuarioDto de(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNombre(), u.getApellidos(), u.getCorreo(), u.getRol().valor());
    }
}