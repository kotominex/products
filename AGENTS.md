# Products API — Contexto del proyecto

## Stack
- **Java 21**, Spring Boot 4.0.6
- Spring WebMVC, Data JPA, Security, Validation
- PostgreSQL 18, JJWT 0.12.6, Apache PDFBox 3.0.2, Lombok
- Pruebas: H2, JUnit 5 + Mockito + MockMvc

## Módulos

### Entidades (`entity/`)
| Clase | Tabla | PK | Notas |
|---|---|---|---|
| `Producto` | `productos` | `producto_id` (Long) | FK → empresa, categoria |
| `Empresa` | `empresas` | `nit` (String) | — |
| `Usuario` | `usuarios` | `usuario_id` (Long) | FK → rol |
| `Categoria` | `categorias` | `id_categoria` (Long) | nombre único |
| `Rol` | `roles` | `rol_id` (Long) | nombre único |
| `Orden` | `ordenes` | `orden_id` (Long) | FK → usuario, producto |

### Controladores (`controller/`)
- **`AuthController`** — `POST /auth/registro`, `POST /auth/login` → JWT
- **`EmpresaController`** — CRUD empresas + productos por NIT + PDF
- **`ProductoController`** — `POST /api/productos`
- **`GlobalExceptionHandler`** — `EntityNotFoundException`→400, `BadCredentialsException`→401, `MethodArgumentNotValidException`→400

### Servicios (`service/`)
- **`EmpresaService`** — crear, obtener por NIT, listar todas
- **`ProductoService`** — crear producto (valida empresa y categoría), listar por empresa
- **`PdfService`** — genera PDF de inventario con precios COP/USD/EUR (PDFBox)

### Seguridad (`security/`)
- **`SecurityConfig`** — stateless, `/auth/**` público, resto autenticado
- **`JwtAuthFilter`** — extrae token Bearer, valida JWT, setea SecurityContext
- **`JwtUtil`** — genera/valida JWT con HMAC-SHA (clave 256 bits), extrae email del subject
- **`CustomUserDetailsService`** — carga usuario por email

### DTOs (`dto/`)
- Records: `LoginRequest`, `RegistroRequest`, `TokenResponse`, `EmpresaRequest`, `ProductoRequest`, `ProductoResponse`
- `ProductoResponse.fromEntity()` — convierte `Producto` a response con precios convertidos

### Repositorios (`repository/`)
- Spring Data JPA. `ProductoRepository.findByEmpresa_Nit(String)`

## Endpoints

### Públicos
| Método | Ruta | Body |
|---|---|---|
| POST | `/auth/registro` | `{ nombres, apellidos, email, telefono, password, rolId }` |
| POST | `/auth/login` | `{ email, password }` → JWT |

### Protegidos (Bearer token)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/empresas` | Listar empresas |
| GET | `/api/empresas/{nit}` | Empresa por NIT |
| POST | `/api/empresas` | Crear empresa |
| GET | `/api/empresas/{nit}/productos` | Productos (precios COP/USD/EUR) |
| GET | `/api/empresas/{nit}/productos/pdf` | PDF inventario |
| POST | `/api/productos` | Crear producto |

## Config (`application.properties`)
- `spring.datasource.url=jdbc:postgresql://localhost:5432/products`
- `spring.datasource.username=liteThinking`
- `spring.datasource.password=liteThinking.2026`
- `spring.jpa.hibernate.ddl-auto=validate`
- `app.jwt.secret=clave-secreta-temporal-cambiar-en-produccion-de-al-menos-256-bits`
- `app.jwt.expiration=86400000`
- `app.tasa.cop-usd=0.00026`
- `app.tasa.cop-eur=0.00024`

## Pruebas
- Framework: JUnit 5 + Mockito, H2 DB para tests de repositorio
- Tests: `mvnw test` (requiere `JAVA_HOME` apuntando a JDK 21)
- 12 clases de test cubriendo controllers, services, repos, security

## Compilación y ejecución
```bash
JAVA_HOME=<ruta-jdk21> ./mvnw clean compile
JAVA_HOME=<ruta-jdk21> ./mvnw spring-boot:run
JAVA_HOME=<ruta-jdk21> ./mvnw test
JAVA_HOME=<ruta-jdk21> ./mvnw clean package
```

## Notas
- No existe `frontend/` en el repositorio (README lo menciona como proyecto Vue 3 + Vite separado)
- El artifact `spring-boot-starter-webmvc` en pom.xml no es estándar de Spring Boot 4
- Colección Postman en `src/main/resources/static/Products API.postman_collection.json`
- Script de datos de prueba en `src/main/resources/static/ScriptPruebas.sql`
