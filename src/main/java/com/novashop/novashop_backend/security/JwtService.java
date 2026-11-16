package com.novashop.novashop_backend.security;

import com.novashop.novashop_backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private final SecretKey clave;
    private final long accessMs;
    private final long refreshMs;

    public JwtService(@Value("${novashop.jwt.secret}") String secreto,
                      @Value("${novashop.jwt.access-minutes:15}") long accessMinutos,
                      @Value("${novashop.jwt.refresh-days:7}") long refreshDias) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.accessMs = accessMinutos * 60 * 1000L;
        this.refreshMs = refreshDias * 24 * 60 * 60 * 1000L;
    }

    public String generar(Usuario usuario, String tipo) {
        long duracion = ACCESS.equals(tipo) ? accessMs : refreshMs;
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("rol", usuario.getRol().name())
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracion))
                .signWith(clave)
                .compact();
    }

    /** Lanza JwtException si el token está vencido, alterado o mal formado. */
    public Claims leer(String token) {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }

    public long segundosAccess() {
        return accessMs / 1000;
    }
}