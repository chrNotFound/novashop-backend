package com.novashop.novashop_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private int precio;

    @Column(nullable = false)
    private int stock;

    @Column(length = 120)
    private String atributo;

    @Column(nullable = false)
    private int descuento;

    @Column(nullable = false, length = 300)
    private String imagen;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public int precioFinal() {
        if (descuento > 0) {
            return (int) Math.round(precio * (1 - descuento / 100.0));
        }
        return precio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getAtributo() { return atributo; }
    public void setAtributo(String atributo) { this.atributo = atributo; }
    public int getDescuento() { return descuento; }
    public void setDescuento(int descuento) { this.descuento = descuento; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}