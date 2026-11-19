package com.novashop.novashop_backend.exception;

import com.novashop.novashop_backend.dto.ErrorResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        ErrorResponse cuerpo = new ErrorResponse(LocalDateTime.now(), 400, "Solicitud inválida",
                "Hay datos que no cumplen las validaciones.", campos);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> solicitudInvalida(Exception ex) {
        String mensaje = ex instanceof IllegalArgumentException
                ? ex.getMessage()
                : "El cuerpo o los parámetros de la solicitud no son válidos.";
        return ResponseEntity.badRequest().body(ErrorResponse.de(400, "Solicitud inválida", mensaje));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.de(404, "No encontrado", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException ex) {
        HttpStatus estado = ex.getEstado();
        return ResponseEntity.status(estado)
                .body(ErrorResponse.de(estado.value(), estado.getReasonPhrase(), ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> credenciales(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.de(401, "No autorizado", "Correo o contraseña incorrectos."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.de(403, "Prohibido", "No tienes permiso para realizar esta acción."));
    }
}