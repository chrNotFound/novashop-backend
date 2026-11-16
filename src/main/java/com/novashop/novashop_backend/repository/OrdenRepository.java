package com.novashop.novashop_backend.repository;

import com.novashop.novashop_backend.model.Orden;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findAllByOrderByIdDesc();
    List<Orden> findByUsuarioIdOrderByIdDesc(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}