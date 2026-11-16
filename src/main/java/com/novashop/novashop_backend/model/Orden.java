package com.novashop.novashop_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Column(nullable = false)
    private int total;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "cliente_nombre", nullable = false, length = 80)
    private String clienteNombre;
    @Column(name = "cliente_apellidos", nullable = false, length = 80)
    private String clienteApellidos;
    @Column(name = "cliente_correo", nullable = false, length = 120)
    private String clienteCorreo;

    @Column(nullable = false, length = 150)
    private String calle;
    @Column(length = 30)
    private String departamento;
    @Column(nullable = false, length = 80)
    private String region;
    @Column(nullable = false, length = 80)
    private String comuna;
    @Column(length = 300)
    private String indicaciones;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrdenItem> items = new ArrayList<>();

    public void agregarItem(OrdenItem item) {
        item.setOrden(this);
        items.add(item);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String v) { this.clienteNombre = v; }
    public String getClienteApellidos() { return clienteApellidos; }
    public void setClienteApellidos(String v) { this.clienteApellidos = v; }
    public String getClienteCorreo() { return clienteCorreo; }
    public void setClienteCorreo(String v) { this.clienteCorreo = v; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
    public List<OrdenItem> getItems() { return items; }
    public void setItems(List<OrdenItem> items) { this.items = items; }
}