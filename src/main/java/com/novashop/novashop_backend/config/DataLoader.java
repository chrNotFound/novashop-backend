package com.novashop.novashop_backend.config;

import com.novashop.novashop_backend.model.Categoria;
import com.novashop.novashop_backend.model.Producto;
import com.novashop.novashop_backend.model.Rol;
import com.novashop.novashop_backend.model.Usuario;
import com.novashop.novashop_backend.repository.CategoriaRepository;
import com.novashop.novashop_backend.repository.ProductoRepository;
import com.novashop.novashop_backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;
    private final ProductoRepository productos;
    private final PasswordEncoder encoder;

    public DataLoader(UsuarioRepository usuarios, CategoriaRepository categorias,
                      ProductoRepository productos, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.categorias = categorias;
        this.productos = productos;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() == 0) {
            crearUsuario("Admin", "NovaShop", "admin@novashop.cl", "Admin123", Rol.ADMIN);
            crearUsuario("Cliente", "Demo", "cliente@novashop.cl", "Cliente123", Rol.CLIENTE);
        }
        if (productos.count() == 0) {
            producto("Audifonos Gamer Inalambrico HyperX Cloud III", 99990, 15, "Cable 3.5mm / USB-C", "Audio", 15,
                    "/Imagen/AudifonosHyperXCloudIII.jpg",
                    "Audifonos gamer inalambricos con sonido envolvente, microfono desmontable y hasta 120 horas de bateria. Compatibles con PC, PS5 y Nintendo Switch.");
            producto("Corsair Memoria RAM Vengeance RGB DDR5", 549990, 10, "16GB (2x8GB) 5200MHz", "Componentes PC", 0,
                    "/Imagen/Corsair Memoria RAM Vengeance RGB DDR5.jpg",
                    "Memoria RAM DDR5 con iluminacion RGB dinamica, ideal para equipos gamer y de alto rendimiento.");
            producto("Kingston FURY Beast Memoria RAM DDR4", 145990, 20, "16GB 3200MHz", "Componentes PC", 10,
                    "/Imagen/Kingston FURY Beast Memoria RAM DDR4.jpg",
                    "Memoria RAM DDR4 de alto rendimiento con disipador de calor, compatible con la mayoria de placas madre.");
            producto("Mouse Gamer Logitech G305 Lightspeed Wireless", 29990, 25, "12.000 DPI Inalambrico", "Perifericos", 0,
                    "/Imagen/Mouse Gamer logitech G305 Lightspeed Wireless negro.jpg",
                    "Mouse gamer inalambrico con sensor de alta precision y hasta 250 horas de autonomia con una sola pila.");
            producto("Sony WH-CH520", 34990, 18, "Bluetooth / 50h autonomia", "Audio", 20,
                    "/Imagen/Sony WH CH520.jpg",
                    "Audifonos inalambricos livianos, comodos para uso diario, con hasta 50 horas de bateria.");
            producto("Teclado Gamer Redragon Kumara K552 RGB TKL", 34990, 12, "Mecanico Switch Red", "Perifericos", 0,
                    "/Imagen/Teclado Gamer Redragon Kumara K552 RGB TKL.jpg",
                    "Teclado mecanico TKL con retroiluminacion RGB y switches red, ideal para gaming y escritura rapida.");
            producto("Xiaomi Redmi Buds 6 Play", 12990, 30, "Bluetooth / 36h con estuche", "Audio", 25,
                    "/Imagen/Xiaomi Redmi Buds 6 Play.jpg",
                    "Audifonos inalambricos compactos con estuche de carga, resistencia al agua y buena calidad de sonido.");
            producto("Xiaomi Redmi Watch 5 Active", 34990, 14, "Pantalla 2\" / 5 ATM", "Wearables", 0,
                    "/Imagen/Xiaomi Redmi Watch 5 Active.jpg",
                    "Smartwatch con pantalla de 2 pulgadas, resistencia al agua 5 ATM y mas de 100 modos deportivos.");
            producto("Audifonos Inalambricos con Cancelacion de Ruido", 45990, 16, "Bluetooth / ANC", "Audio", 0,
                    "/Imagen/AudifonosCanceRuido.jpg",
                    "Audifonos over-ear con cancelacion activa de ruido, ideales para viajes, estudio y oficina.");
            producto("Cargador para Notebook Universal", 19990, 22, "65W / Multiples conectores", "Componentes PC", 0,
                    "/Imagen/cargadorPc.jpg",
                    "Cargador universal para notebook de 65W, compatible con distintas marcas y modelos.");
        }
    }

    private void crearUsuario(String nombre, String apellidos, String correo, String clave, Rol rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setCorreo(correo);
        u.setClave(encoder.encode(clave));
        u.setRol(rol);
        usuarios.save(u);
    }

    private void producto(String nombre, int precio, int stock, String atributo, String categoria,
                          int descuento, String imagen, String descripcion) {
        Categoria cat = categorias.findByNombreIgnoreCase(categoria).orElseGet(() -> {
            Categoria nueva = new Categoria();
            nueva.setNombre(categoria);
            return categorias.save(nueva);
        });
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setAtributo(atributo);
        p.setDescuento(descuento);
        p.setImagen(imagen);
        p.setDescripcion(descripcion);
        p.setCategoria(cat);
        productos.save(p);
    }
}