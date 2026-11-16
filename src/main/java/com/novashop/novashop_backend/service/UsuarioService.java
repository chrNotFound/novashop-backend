package com.novashop.novashop_backend.service;

import com.novashop.novashop_backend.dto.PerfilRequest;
import com.novashop.novashop_backend.dto.UsuarioDto;
import com.novashop.novashop_backend.dto.UsuarioRequest;
import com.novashop.novashop_backend.exception.RecursoNoEncontradoException;
import com.novashop.novashop_backend.exception.ReglaNegocioException;
import com.novashop.novashop_backend.model.Rol;
import com.novashop.novashop_backend.model.Usuario;
import com.novashop.novashop_backend.repository.OrdenRepository;
import com.novashop.novashop_backend.repository.UsuarioRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final OrdenRepository ordenes;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, OrdenRepository ordenes, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.ordenes = ordenes;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listar() {
        return usuarios.findAll().stream().map(UsuarioDto::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDto obtener(Long id) {
        return UsuarioDto.de(buscar(id));
    }

    @Transactional
    public UsuarioDto crear(UsuarioRequest request) {
        if (request.clave() == null || request.clave().isBlank()) {
            throw new ReglaNegocioException(HttpStatus.BAD_REQUEST, "Ingresa una contraseña.");
        }
        String correo = request.correo().trim();
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El correo ya está registrado.");
        }
        Usuario u = new Usuario();
        u.setNombre(request.nombre().trim());
        u.setApellidos(request.apellidos().trim());
        u.setCorreo(correo);
        u.setClave(encoder.encode(request.clave()));
        u.setRol(Rol.desde(request.rol()));
        return UsuarioDto.de(usuarios.save(u));
    }

    @Transactional
    public UsuarioDto actualizar(Long id, UsuarioRequest request) {
        Usuario u = buscar(id);
        String correo = request.correo().trim();
        usuarios.findByCorreoIgnoreCase(correo).ifPresent(otro -> {
            if (!otro.getId().equals(id)) {
                throw new ReglaNegocioException("El correo ya está registrado.");
            }
        });
        u.setNombre(request.nombre().trim());
        u.setApellidos(request.apellidos().trim());
        u.setCorreo(correo);
        u.setRol(Rol.desde(request.rol()));
        if (request.clave() != null && !request.clave().isBlank()) {
            u.setClave(encoder.encode(request.clave()));
        }
        return UsuarioDto.de(usuarios.save(u));
    }

    @Transactional
    public void eliminar(Long id, String correoActual) {
        Usuario u = buscar(id);
        if (u.getCorreo().equalsIgnoreCase(correoActual)) {
            throw new ReglaNegocioException("No puedes eliminar tu propia cuenta.");
        }
        if (ordenes.existsByUsuarioId(id)) {
            throw new ReglaNegocioException("No se puede eliminar un usuario que tiene compras registradas.");
        }
        usuarios.delete(u);
    }

    @Transactional
    public UsuarioDto actualizarPerfil(String correo, PerfilRequest request) {
        Usuario u = usuarios.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado."));
        u.setNombre(request.nombre().trim());
        u.setApellidos(request.apellidos().trim());
        if (request.clave() != null && !request.clave().isBlank()) {
            u.setClave(encoder.encode(request.clave()));
        }
        return UsuarioDto.de(usuarios.save(u));
    }

    private Usuario buscar(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }
}