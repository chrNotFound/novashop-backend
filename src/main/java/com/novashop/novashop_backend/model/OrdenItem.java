package com.novashop.novashop_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orden_items")
public class OrdenItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "orden_id")
    private Orden orden;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private int precio;

    @Column(nullable = false)
    private int cantidad;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}