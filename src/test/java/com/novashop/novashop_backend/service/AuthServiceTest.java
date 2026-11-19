package com.novashop.novashop_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.novashop.novashop_backend.dto.AuthResponse;
import com.novashop.novashop_backend.dto.LoginRequest;
import com.novashop.novashop_backend.dto.RegistroRequest;
import com.novashop.novashop_backend.exception.ReglaNegocioException;
import com.novashop.novashop_backend.model.Rol;
import com.novashop.novashop_backend.model.Usuario;
import com.novashop.novashop_backend.repository.UsuarioRepository;
import com.novashop.novashop_backend.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SECRETO = "clave-de-prueba-con-mas-de-32-caracteres-123456";

    @Mock
    private UsuarioRepository usuarios;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtService jwt = new JwtService(SECRETO, 15, 7);
    private AuthService service;

    @BeforeEach
    void preparar() {
        service = new AuthService(usuarios, encoder, jwt);
    }

    @Test
    @DisplayName("AU-01 El registro público siempre crea un cliente y entrega tokens")
    void registrarCreaCliente() {
        when(usuarios.existsByCorreoIgnoreCase("nuevo@novashop.cl")).thenReturn(false);
        when(usuarios.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setId(10L);
            return u;
        });

        AuthResponse respuesta = service.registrar(
                new RegistroRequest("Ana", "Pérez", "nuevo@novashop.cl", "Clave123"));

        assertEquals("cliente", respuesta.usuario().rol());
        assertNotNull(respuesta.accessToken());
        assertEquals("Bearer", respuesta.tipo());
    }

    @Test
    @DisplayName("AU-02 No se puede registrar un correo repetido")
    void registrarCorreoRepetido() {
        when(usuarios.existsByCorreoIgnoreCase("cliente@novashop.cl")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.registrar(
                new RegistroRequest("Ana", "Pérez", "cliente@novashop.cl", "Clave123")));
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("AU-03 Login con contraseña incorrecta es rechazado")
    void loginClaveIncorrecta() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setCorreo("admin@novashop.cl");
        u.setClave(encoder.encode("Admin123"));
        u.setRol(Rol.ADMIN);
        when(usuarios.findByCorreoIgnoreCase("admin@novashop.cl")).thenReturn(Optional.of(u));

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("admin@novashop.cl", "otraClave1")));
    }

    @Test
    @DisplayName("AU-04 El access token lleva el correo y el rol del usuario")
    void tokenConRol() {
        Usuario u = new Usuario();
        u.setCorreo("admin@novashop.cl");
        u.setRol(Rol.ADMIN);

        var claims = jwt.leer(jwt.generar(u, JwtService.ACCESS));

        assertEquals("admin@novashop.cl", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol", String.class));
        assertEquals(JwtService.ACCESS, claims.get("tipo", String.class));
    }
}
