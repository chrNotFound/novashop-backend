package com.novashop.novashop_backend.service;

import com.novashop.novashop_backend.dto.AuthResponse;
import com.novashop.novashop_backend.dto.LoginRequest;
import com.novashop.novashop_backend.dto.RegistroRequest;
import com.novashop.novashop_backend.dto.UsuarioDto;
import com.novashop.novashop_backend.exception.ReglaNegocioException;
import com.novashop.novashop_backend.model.Rol;
import com.novashop.novashop_backend.model.Usuario;
import com.novashop.novashop_backend.repository.UsuarioRepository;
import com.novashop.novashop_backend.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario u = usuarios.findByCorreoIgnoreCase(request.correo().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!encoder.matches(request.clave(), u.getClave())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        return respuesta(u);
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        String correo = request.correo().trim();
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El correo ya está registrado.");
        }
        Usuario u = new Usuario();
        u.setNombre(request.nombre().trim());
        u.setApellidos(request.apellidos().trim());
        u.setCorreo(correo);
        u.setClave(encoder.encode(request.clave()));
        u.setRol(Rol.CLIENTE); // el registro público siempre crea clientes
        return respuesta(usuarios.save(u));
    }

    @Transactional(readOnly = true)
    public AuthResponse renovar(String refreshToken) {
        try {
            Claims claims = jwt.leer(refreshToken);
            if (!JwtService.REFRESH.equals(claims.get("tipo", String.class))) {
                throw new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "El token no es un refresh token.");
            }
            Usuario u = usuarios.findByCorreoIgnoreCase(claims.getSubject())
                    .orElseThrow(() -> new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado."));
            return respuesta(u);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "Refresh token inválido o vencido.");
        }
    }

    @Transactional(readOnly = true)
    public UsuarioDto yo(String correo) {
        return usuarios.findByCorreoIgnoreCase(correo)
                .map(UsuarioDto::de)
                .orElseThrow(() -> new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado."));
    }

    private AuthResponse respuesta(Usuario u) {
        return new AuthResponse(jwt.generar(u, JwtService.ACCESS), jwt.generar(u, JwtService.REFRESH),
                "Bearer", jwt.segundosAccess(), UsuarioDto.de(u));
    }
}