# Products API

API REST para gestión de inventario de productos con autenticación JWT.  
Proyecto desarrollado por **LiteThinking**.

---

## Stack tecnológico

### Backend

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Web MVC | — |
| Spring Data JPA | — |
| Spring Security | 7.0.5 |
| PostgreSQL | 18 (driver runtime) |
| Lombok | — |
| JJWT | 0.12.6 |
| Apache PDFBox | 3.0.2 |
| Maven Wrapper | 3.9.16 |

### Frontend

| Tecnología | Versión |
|---|---|
| Vue | 3.5 |
| Vite | 6.3 |
| Vue Router | 4.5 |
| Pinia | 3.0 |
| Axios | 1.7 |
| PrimeVue | 4.3 |

---

## Requisitos previos

- **JDK 21** (el `JAVA_HOME` del sistema apunta a JDK 8; anteponer `JAVA_HOME=<ruta-jdk21>` a los comandos de Maven)
- **PostgreSQL 18** corriendo en `localhost:5432`, base de datos `products_db`
- **Node.js** 18+ y **npm**
- **Maven Wrapper** incluido en el repositorio (`mvnw` / `mvnw.cmd`)

---

## Ejecución

### 1. Base de datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE products_db;
```

Las tablas se crean automáticamente al iniciar la aplicación (configuración `create-drop` en `application.properties`).

### 2. Backend (Spring Boot)

```bash
# Compilar
JAVA_HOME=<ruta-jdk21> ./mvnw clean compile

# Ejecutar (puerto 8080)
JAVA_HOME=<ruta-jdk21> ./mvnw spring-boot:run

# Tests
JAVA_HOME=<ruta-jdk21> ./mvnw test

# Empaquetar
JAVA_HOME=<ruta-jdk21> ./mvnw clean package
```

El backend arranca en `http://localhost:8080`.

### 3. Frontend (Vue + Vite)

```bash
cd frontend

# Instalar dependencias
npm install

# Iniciar dev server (puerto 5173)
npm run dev

# Compilar para producción
npm run build
```

El frontend se sirve en `http://localhost:5173`. Las peticiones a `/auth/*` y `/api/*` se redirigen automáticamente al backend en `:8080` mediante el proxy configurado en `vite.config.js`.

---

## Endpoints

### Públicos

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/auth/registro` | Registrar usuario (body: `{ nombres, apellidos, email, telefono, password, rolId }`) |
| `POST` | `/auth/login` | Iniciar sesión (body: `{ email, password }`) → devuelve JWT |

### Protegidos (requieren `Authorization: Bearer <jwt>`)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/empresas` | Listar todas las empresas |
| `GET` | `/api/empresas/{nit}` | Obtener empresa por NIT |
| `POST` | `/api/empresas` | Crear empresa |
| `GET` | `/api/empresas/{nit}/productos` | Listar productos de una empresa (precios en COP, USD, EUR) |
| `GET` | `/api/empresas/{nit}/productos/pdf` | Descargar inventario en PDF |
| `POST` | `/api/productos` | Crear producto |

---

## Recursos

| Recurso | Ruta |
|---|---|
| Colección Postman | `src/main/resources/static/Products API.postman_collection.json` |
| Imagen fondo login | `frontend/src/images/fondo_login.png` |
| Configuración base de datos | `src/main/resources/application.properties` |
| Configuración JWT | `src/main/resources/application.properties` (`app.jwt.*`) |
| Tasas de cambio COP → USD/EUR | `src/main/resources/application.properties` (`app.tasa.*`) |

### Colección Postman

Importar en Postman el archivo `src/main/resources/static/Products API.postman_collection.json`.  
Incluye variables de entorno (`baseUrl`, `token`) y script que guarda automáticamente el JWT al hacer login. Flujo sugerido:

1. Crear un rol en BD (ej. `INSERT INTO roles (nombre) VALUES ('ADMIN');`)
2. Ejecutar `POST /auth/registro` con `rolId` correspondiente
3. Ejecutar `POST /auth/login` (el token se guarda automáticamente)
4. Consumir endpoints protegidos

---

## Estructura del proyecto

```
products/
├── src/main/java/com/liteThinking/products/
│   ├── controller/       # Controladores REST
│   ├── dto/              # DTOs de entrada/salida
│   ├── entity/           # Entidades JPA
│   ├── repository/       # Repositorios Spring Data
│   ├── security/         # Config JWT, filtros, UserDetailsService
│   └── service/          # Lógica de negocio
├── src/main/resources/
│   ├── application.properties
│   └── static/           # Colección Postman
└── frontend/
    ├── src/
    │   ├── components/   # Componentes Vue reutilizables
    │   ├── router/       # Configuración de rutas
    │   ├── services/     # Cliente Axios con interceptor JWT
    │   ├── stores/       # Stores Pinia (autenticación)
    │   └── views/        # Páginas (Login, Registro, Empresas, Productos)
    └── package.json
```
