package com.novashop.novashop_backend.service;

import com.novashop.novashop_backend.dto.OrdenDto;
import com.novashop.novashop_backend.dto.OrdenRequest;
import com.novashop.novashop_backend.exception.RecursoNoEncontradoException;
import com.novashop.novashop_backend.exception.ReglaNegocioException;
import com.novashop.novashop_backend.model.EstadoOrden;
import com.novashop.novashop_backend.model.Orden;
import com.novashop.novashop_backend.model.OrdenItem;
import com.novashop.novashop_backend.model.Producto;
import com.novashop.novashop_backend.model.Usuario;
import com.novashop.novashop_backend.repository.OrdenRepository;
import com.novashop.novashop_backend.repository.ProductoRepository;
import com.novashop.novashop_backend.repository.UsuarioRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenService {

    private final OrdenRepository ordenes;
    private final ProductoRepository productos;
    private final UsuarioRepository usuarios;

    public OrdenService(OrdenRepository ordenes, ProductoRepository productos, UsuarioRepository usuarios) {
        this.ordenes = ordenes;
        this.productos = productos;
        this.usuarios = usuarios;
    }


    @Transactional
    public OrdenDto crear(OrdenRequest request, String correoUsuario) {
        Usuario usuario = usuarios.findByCorreoIgnoreCase(correoUsuario)
                .orElseThrow(() -> new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado."));
        boolean aprobado = !"rechazado".equalsIgnoreCase(request.resultadoPago());

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(aprobado ? EstadoOrden.PAGADA : EstadoOrden.RECHAZADA);
        orden.setClienteNombre(request.cliente().nombre().trim());
        orden.setClienteApellidos(request.cliente().apellidos().trim());
        orden.setClienteCorreo(request.cliente().correo().trim());
        orden.setCalle(request.direccion().calle().trim());
        orden.setDepartamento(texto(request.direccion().departamento()));
        orden.setRegion(request.direccion().region().trim());
        orden.setComuna(request.direccion().comuna().trim());
        orden.setIndicaciones(texto(request.direccion().indicaciones()));

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        request.items().forEach(i -> cantidades.merge(i.id(), i.cantidad(), Integer::sum));

        int total = 0;
        for (Map.Entry<Long, Integer> linea : cantidades.entrySet()) {
            Producto p = productos.findById(linea.getKey())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + linea.getKey()));
            int cantidad = linea.getValue();
            if (aprobado) {
                descontarStock(p, cantidad);
            }
            OrdenItem item = new OrdenItem();
            item.setProductoId(p.getId());
            item.setNombre(p.getNombre());
            item.setPrecio(p.precioFinal());
            item.setCantidad(cantidad);
            orden.agregarItem(item);
            total += p.precioFinal() * cantidad;
        }
        orden.setTotal(total);
        return OrdenDto.de(ordenes.save(orden));
    }

    @Transactional(readOnly = true)
    public List<OrdenDto> listar(Long usuarioId) {
        List<Orden> lista = usuarioId == null
                ? ordenes.findAllByOrderByIdDesc()
                : ordenes.findByUsuarioIdOrderByIdDesc(usuarioId);
        return lista.stream().map(OrdenDto::de).toList();
    }

    @Transactional(readOnly = true)
    public List<OrdenDto> listarMias(String correo) {
        Usuario u = usuarios.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado."));
        return ordenes.findByUsuarioIdOrderByIdDesc(u.getId()).stream().map(OrdenDto::de).toList();
    }

    @Transactional(readOnly = true)
    public OrdenDto obtener(Long id, String correo, boolean esAdmin) {
        Orden o = buscar(id);
        if (!esAdmin && !o.getUsuario().getCorreo().equalsIgnoreCase(correo)) {
            throw new ReglaNegocioException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta orden.");
        }
        return OrdenDto.de(o);
    }

    @Transactional
    public OrdenDto cambiarEstado(Long id, String estadoTexto) {
        EstadoOrden nuevo = EstadoOrden.desde(estadoTexto);
        Orden o = buscar(id);
        EstadoOrden actual = o.getEstado();
        if (actual != nuevo) {
            if (actual.descuentaStock() && !nuevo.descuentaStock()) {
                for (OrdenItem item : o.getItems()) {
                    productos.findById(item.getProductoId()).ifPresent(p -> {
                        p.setStock(p.getStock() + item.getCantidad());
                        productos.save(p);
                    });
                }
            } else if (!actual.descuentaStock() && nuevo.descuentaStock()) {
                for (OrdenItem item : o.getItems()) {
                    Producto p = productos.findById(item.getProductoId())
                            .orElseThrow(() -> new RecursoNoEncontradoException(
                                    "El producto " + item.getNombre() + " ya no existe."));
                    descontarStock(p, item.getCantidad());
                }
            }
            o.setEstado(nuevo);
            o = ordenes.save(o);
        }
        return OrdenDto.de(o);
    }

    private void descontarStock(Producto p, int cantidad) {
        if (p.getStock() < cantidad) {
            throw new ReglaNegocioException("Stock insuficiente para \"" + p.getNombre()
                    + "\". Disponible: " + p.getStock() + ", solicitado: " + cantidad + ".");
        }
        p.setStock(p.getStock() - cantidad);
        productos.save(p);
    }

    private Orden buscar(Long id) {
        return ordenes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada: " + id));
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}