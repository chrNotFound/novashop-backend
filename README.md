# NovaShop Backend

API REST de la tienda online **NovaShop**, hecha con **Spring Boot 4, Spring Security, JWT, JPA/Hibernate y MySQL**.
Atiende al frontend en React de NovaShop y está documentada con Swagger.

Proyecto de la Evaluación Parcial 3 de DSY1104 Desarrollo Fullstack II.

## Tecnologías

| Capa | Herramienta |
| --- | --- |
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security) |
| Base de datos | MySQL 8 (H2 en memoria para las pruebas) |
| Autenticación | JWT con jjwt 0.12 (access token de 15 min + refresh token de 7 días) |
| Documentación | springdoc-openapi 3 (Swagger UI) |
| Pruebas | JUnit 5, Mockito y MockMvc |
| Build | Maven Wrapper |

## Estructura

```
src/main/java/com/novashop/novashop_backend
├── config        DataLoader (datos iniciales) y OpenApiConfig (Swagger + esquema Bearer)
├── controller    Controladores REST (auth, productos, categorías, órdenes, usuarios, perfil)
├── dto           Objetos de entrada (Request) y salida (Dto) con validaciones
├── exception     Excepciones de negocio y manejador global de errores
├── model         Entidades JPA: Usuario, Categoria, Producto, Orden, OrdenItem + enums Rol y EstadoOrden
├── repository    Repositorios Spring Data JPA
├── security      SecurityConfig, JwtService y JwtFilter
└── service       Lógica de negocio
```

## Requisitos

- JDK 21
- MySQL 8 corriendo en `localhost:3306` (XAMPP, Laragon o MySQL Server)

## Cómo ejecutar

```bash
git clone https://github.com/jael716/novashop-backend.git
cd novashop-backend

# Windows
mvnw.cmd spring-boot:run
# Linux / macOS
./mvnw spring-boot:run
```

La base `novashop` se crea sola la primera vez (`createDatabaseIfNotExist=true`) y las tablas se generan con
`ddl-auto=update`. Al iniciar, `DataLoader` carga dos usuarios de demostración, cuatro categorías y diez productos.

### Variables de entorno (opcionales)

| Variable | Valor por defecto |
| --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/novashop?...` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(vacío)* |
| `JWT_SECRET` | clave de desarrollo (mínimo 32 caracteres) |
| `CORS_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` |
| `PORT` | `8080` |

## Usuarios de demostración

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Administrador | admin@novashop.cl | Admin123 |
| Cliente | cliente@novashop.cl | Cliente123 |

Las contraseñas se guardan cifradas con BCrypt.

## Documentación de la API

Con el backend encendido:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Para probar endpoints protegidos: ejecuta `POST /api/auth/login`, copia el `accessToken` y pégalo en **Authorize**.

## Endpoints

| Método | Ruta | Acceso | Descripción |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Público | Inicia sesión y entrega access y refresh token |
| POST | `/api/auth/register` | Público | Crea una cuenta con rol cliente |
| POST | `/api/auth/refresh` | Público | Renueva el access token |
| GET | `/api/auth/me` | Autenticado | Datos del usuario conectado |
| GET | `/api/productos` · `/api/productos/{id}` | Público | Catálogo y detalle |
| POST · PUT · DELETE | `/api/productos[/{id}]` | Admin | CRUD de productos |
| GET | `/api/categorias` · `/api/categorias/{id}` | Público | Categorías con su cantidad de productos |
| POST · PUT · DELETE | `/api/categorias[/{id}]` | Admin | CRUD de categorías (no se borra si tiene productos) |
| POST | `/api/ordenes` | Autenticado | Crea una compra: recalcula precios y descuenta stock |
| GET | `/api/ordenes/mias` | Autenticado | Compras del usuario conectado |
| GET | `/api/ordenes/{id}` | Dueño o admin | Detalle (boleta) de una orden |
| GET | `/api/ordenes?usuarioId=` | Admin | Todas las órdenes o las de un usuario |
| PATCH | `/api/ordenes/{id}/estado` | Admin | Cambia el estado y ajusta el stock |
| GET · POST · PUT · DELETE | `/api/usuarios[/{id}]` | Admin | CRUD de usuarios |
| PUT | `/api/perfil` | Autenticado | Edita nombre, apellidos y contraseña propios |

### Formato de errores

```json
{ "fecha": "2026-11-19T17:30:00", "estado": 400, "error": "Solicitud inválida",
  "mensaje": "Hay datos que no cumplen las validaciones.", "campos": { "precio": "El precio debe ser mayor a 0." } }
```

| Código | Cuándo |
| --- | --- |
| 400 | Validación fallida o JSON mal formado |
| 401 | Sin token, token vencido o credenciales incorrectas |
| 403 | El rol no tiene permiso |
| 404 | Recurso inexistente |
| 409 | Regla de negocio (correo repetido, stock insuficiente, categoría con productos) |

## Modelo de datos

```
usuarios 1 ──< ordenes 1 ──< orden_items >── productos >── 1 categorias
```

- **usuarios**: id, nombre, apellidos, correo (único), clave (BCrypt), rol (`ADMIN`/`CLIENTE`)
- **categorias**: id, nombre (único)
- **productos**: id, nombre, precio, descuento, stock, atributo, imagen, descripción, categoria_id
- **ordenes** (boleta): id, fecha, estado, total, usuario_id, datos del cliente y dirección de entrega
- **orden_items** (detalle de boleta): id, orden_id, producto_id, nombre, precio final, cantidad

## Pruebas

```bash
./mvnw test        # Windows: mvnw.cmd test
```

Las pruebas no necesitan MySQL: las de integración usan una base H2 en memoria con el perfil `test`
(`src/test/resources/application-test.properties`) y las unitarias no levantan Spring.

| Clase | Tipo | Qué verifica |
| --- | --- | --- |
| `ApiIntegracionTest` | Integración (MockMvc + H2) | Login, registro, 401/403 por rol, CRUD de productos, compras, stock insuficiente, cancelación con reposición de stock |
| `OrdenServiceTest` | Unitaria (Mockito) | Compra aprobada y rechazada, stock insuficiente, reposición al cancelar, acceso a órdenes ajenas, estados inválidos |
| `AuthServiceTest` | Unitaria (Mockito) | Registro como cliente, correo repetido, contraseña incorrecta, contenido del JWT |
| `ReglasModeloTest` | Unitaria | Precio con descuento, estados que descuentan stock, conversión de rol y estado |

Los resultados quedan en `target/surefire-reports/`.

## Autora

Jael Gallardo — [GitHub](https://github.com/jael716)
