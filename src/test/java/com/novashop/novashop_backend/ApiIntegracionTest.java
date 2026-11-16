package com.novashop.novashop_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegracionTest {

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    @Autowired
    private MockMvc mvc;

    private String token(String correo, String clave) throws Exception {
        String cuerpo = mvc.perform(post("/api/auth/login").contentType(JSON)
                        .content("{\"correo\":\"" + correo + "\",\"clave\":\"" + clave + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.accessToken");
    }

    private String tokenAdmin() throws Exception {
        return token("admin@novashop.cl", "Admin123");
    }

    private String tokenCliente() throws Exception {
        return token("cliente@novashop.cl", "Cliente123");
    }

    private String productoJson(int precio, int stock, int descuento) {
        return "{\"nombre\":\"Producto de prueba " + UUID.randomUUID() + "\","
                + "\"categoria\":\"Audio\",\"precio\":" + precio + ",\"stock\":" + stock
                + ",\"descuento\":" + descuento + ",\"atributo\":\"Prueba\","
                + "\"imagen\":\"/Imagen/prueba.jpg\","
                + "\"descripcion\":\"Descripcion de prueba para el test de integracion.\"}";
    }

    private long crearProducto(int precio, int stock) throws Exception {
        String cuerpo = mvc.perform(post("/api/productos").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .content(productoJson(precio, stock, 0)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(cuerpo, "$.id")).longValue();
    }

    private int stockDe(long productoId) throws Exception {
        String cuerpo = mvc.perform(get("/api/productos/" + productoId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(cuerpo, "$.stock")).intValue();
    }

    private String ordenJson(long productoId, int cantidad, String resultadoPago) {
        return "{\"cliente\":{\"nombre\":\"Cliente\",\"apellidos\":\"Demo\",\"correo\":\"cliente@novashop.cl\"},"
                + "\"direccion\":{\"calle\":\"Av. Siempre Viva 123\",\"departamento\":\"\","
                + "\"region\":\"Metropolitana\",\"comuna\":\"Santiago\",\"indicaciones\":\"\"},"
                + "\"items\":[{\"id\":" + productoId + ",\"cantidad\":" + cantidad + "}],"
                + "\"resultadoPago\":\"" + resultadoPago + "\"}";
    }

    private long comprar(long productoId, int cantidad, String resultadoPago) throws Exception {
        String cuerpo = mvc.perform(post("/api/ordenes").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenCliente())
                        .content(ordenJson(productoId, cantidad, resultadoPago)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(cuerpo, "$.id")).longValue();
    }

    @Test
    @DisplayName("Login correcto devuelve tokens y el rol del usuario")
    void loginCorrecto() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(JSON)
                        .content("{\"correo\":\"admin@novashop.cl\",\"clave\":\"Admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.usuario.rol").value("admin"));
    }

    @Test
    @DisplayName("Login con contraseña incorrecta responde 401")
    void loginIncorrecto() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(JSON)
                        .content("{\"correo\":\"admin@novashop.cl\",\"clave\":\"Equivocada1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("El registro público crea siempre un usuario con rol cliente y rechaza correos repetidos")
    void registroCreaCliente() throws Exception {
        String correo = "nuevo" + System.nanoTime() + "@correo.cl";
        String cuerpo = "{\"nombre\":\"Ana\",\"apellidos\":\"Perez\",\"correo\":\"" + correo
                + "\",\"clave\":\"Clave1234\"}";

        mvc.perform(post("/api/auth/register").contentType(JSON).content(cuerpo))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.usuario.rol").value("cliente"));

        mvc.perform(post("/api/auth/register").contentType(JSON).content(cuerpo))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /auth/me devuelve el usuario del token")
    void meConToken() throws Exception {
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + tokenCliente()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("cliente@novashop.cl"));
    }

    @Test
    @DisplayName("Sin token las rutas protegidas responden 401")
    void sinTokenEs401() throws Exception {
        mvc.perform(get("/api/ordenes/mias")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/productos").contentType(JSON).content(productoJson(1000, 1, 0)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un token inválido responde 401")
    void tokenInvalidoEs401() throws Exception {
        mvc.perform(get("/api/usuarios").header("Authorization", "Bearer token.falso.123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un cliente no puede usar rutas de admin (403)")
    void clienteNoEsAdmin() throws Exception {
        String cliente = tokenCliente();
        mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + cliente))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/ordenes").header("Authorization", "Bearer " + cliente))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/productos").contentType(JSON)
                        .header("Authorization", "Bearer " + cliente)
                        .content(productoJson(1000, 1, 0)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un admin sí puede listar usuarios y órdenes")
    void adminListaTodo() throws Exception {
        String admin = tokenAdmin();
        mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
        mvc.perform(get("/api/ordenes").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("El catálogo de productos es público y trae los productos iniciales")
    void catalogoPublico() throws Exception {
        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThanOrEqualTo(10)));
    }

    @Test
    @DisplayName("El admin crea, edita y elimina un producto")
    void crudProducto() throws Exception {
        String admin = tokenAdmin();
        long id = crearProducto(25000, 8);

        mvc.perform(put("/api/productos/" + id).contentType(JSON)
                        .header("Authorization", "Bearer " + admin)
                        .content(productoJson(27000, 9, 0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(27000))
                .andExpect(jsonPath("$.stock").value(9));

        mvc.perform(delete("/api/productos/" + id).header("Authorization", "Bearer " + admin))
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/api/productos/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Un producto con precio 0 se rechaza con 400 y el detalle por campo")
    void productoInvalido() throws Exception {
        mvc.perform(post("/api/productos").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .content(productoJson(0, 5, 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.precio").isNotEmpty());
    }

    @Test
    @DisplayName("No se puede eliminar una categoría que tiene productos (409)")
    void categoriaConProductosNoSeElimina() throws Exception {
        String lista = mvc.perform(get("/api/categorias")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<?> ids = JsonPath.read(lista, "$[?(@.nombre=='Audio')].id");
        long idAudio = ((Number) ids.get(0)).longValue();

        mvc.perform(delete("/api/categorias/" + idAudio)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Una compra aprobada queda pagada, calcula el total en el servidor y descuenta stock")
    void compraAprobadaDescuentaStock() throws Exception {
        long id = crearProducto(10000, 10);

        String cuerpo = mvc.perform(post("/api/ordenes").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenCliente())
                        .content(ordenJson(id, 3, "aprobado")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("pagada"))
                .andExpect(jsonPath("$.total").value(30000))
                .andReturn().getResponse().getContentAsString();

        assertNotNull(JsonPath.read(cuerpo, "$.id"));
        assertEquals(7, stockDe(id));
    }

    @Test
    @DisplayName("Un pago rechazado deja la orden rechazada y no toca el stock")
    void pagoRechazadoNoDescuentaStock() throws Exception {
        long id = crearProducto(10000, 10);

        mvc.perform(post("/api/ordenes").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenCliente())
                        .content(ordenJson(id, 2, "rechazado")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("rechazada"));

        assertEquals(10, stockDe(id));
    }

    @Test
    @DisplayName("Comprar más unidades que el stock disponible responde 409")
    void stockInsuficiente() throws Exception {
        long id = crearProducto(10000, 2);

        mvc.perform(post("/api/ordenes").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenCliente())
                        .content(ordenJson(id, 5, "aprobado")))
                .andExpect(status().isConflict());

        assertEquals(2, stockDe(id));
    }

    @Test
    @DisplayName("Cancelar una orden pagada devuelve el stock")
    void cancelarRestauraStock() throws Exception {
        long id = crearProducto(10000, 10);
        long ordenId = comprar(id, 4, "aprobado");
        assertEquals(6, stockDe(id));

        mvc.perform(patch("/api/ordenes/" + ordenId + "/estado").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .content("{\"estado\":\"cancelada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("cancelada"));

        assertEquals(10, stockDe(id));
    }

    @Test
    @DisplayName("Un cliente no puede cambiar el estado de una orden (403)")
    void clienteNoCambiaEstado() throws Exception {
        long id = crearProducto(10000, 10);
        long ordenId = comprar(id, 1, "aprobado");

        mvc.perform(patch("/api/ordenes/" + ordenId + "/estado").contentType(JSON)
                        .header("Authorization", "Bearer " + tokenCliente())
                        .content("{\"estado\":\"cancelada\"}"))
                .andExpect(status().isForbidden());
    }
}