# Backend Veterinaria San Marcos

Backend Java 21 / Spring Boot para el catálogo, usuarios y panel administrativo. Las órdenes implementadas aquí son **reposiciones internas de stock**. El carrito del frontend sigue siendo local y su botón de pago aún está deshabilitado.

## Servicios

| Servicio | Puerto local | Función |
| --- | ---: | --- |
| Eureka | 8761 | Registro de servicios |
| API Gateway | 8080 | Entrada `/api`, rutas y CORS |
| Usuarios | 8083 | Usuarios, registro, login y JWT |
| Inventario | 8091 | Catálogo, stock, reposiciones y reportes |
| MySQL | 3306 | Dos bases: `db_usuario_service` y `ms_inventario` |

Usuarios e inventario migran sus esquemas mediante Flyway. Eureka y gateway no usan base de datos. El gateway permite por defecto los orígenes Vite `http://localhost:5173` y `http://127.0.0.1:5173`; se pueden cambiar con `CORS_ALLOWED_ORIGINS`.

## Inicio rápido con Docker

1. Copia `.env.example` a `.env` y cambia las contraseñas y el secreto JWT. `AUTH_JWT_SECRET` debe tener por lo menos 32 bytes UTF-8; el mismo valor se comparte entre usuarios e inventario.
2. Ejecuta `docker compose up --build` desde la raíz.
3. Usa `http://localhost:8080` como base de la API. Eureka queda en `http://localhost:8761`.

`ADMIN_EMAIL` y `ADMIN_PASSWORD` crean la cuenta administradora al primer arranque. Los valores del ejemplo corresponden a la demo del frontend; no se recomienda usar ese archivo sin cambiarlo fuera de un entorno local. La cuenta existente nunca se convierte automáticamente en administradora.

Para ejecutar los servicios por separado, usa Java 21, MySQL y el Maven Wrapper de cada carpeta (`.\mvnw.cmd spring-boot:run` en PowerShell). Arranca MySQL, Eureka, usuarios, inventario y gateway en ese orden. Configura `USUARIO_DB_URL`, `USUARIO_DB_USER`, `USUARIO_DB_PASSWORD`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `EUREKA_URL`, `AUTH_JWT_SECRET`, `ADMIN_EMAIL` y `ADMIN_PASSWORD` según tu entorno.

## Rutas principales por el gateway

| Ruta | Uso | Acceso |
| --- | --- | --- |
| `POST /api/usuario/auth/register` | Registro de cliente | Público |
| `POST /api/usuario/auth/login` | Obtener token | Público |
| `GET /api/usuario/auth/me` | Perfil de la sesión | Autenticado |
| `/api/usuario/usuarios` | CRUD de usuarios; `GET /count` para dashboard | ADMIN; lectura también VENDEDOR |
| `/api/productos` | Catálogo y CRUD de productos | Lectura pública; escritura ADMIN/VENDEDOR |
| `/api/categorias` | Categorías | Lectura pública; escritura ADMIN |
| `/api/inventario` | Inventario y stock bajo | ADMIN/VENDEDOR |
| `PATCH /api/inventario/producto/{id}/stock` | Ajuste de stock | ADMIN/VENDEDOR |
| `GET /api/inventario/producto/{id}/movimientos` | Historial de movimientos | ADMIN/VENDEDOR |
| `/api/reposiciones` | Crear/listar reposiciones; recibir/cancelar por ID | ADMIN/VENDEDOR |
| `GET /api/dashboard/resumen` | Productos, stock crítico y órdenes pendientes | ADMIN/VENDEDOR |
| `GET /api/reportes/inventario` | Valor de bodega y desglose | ADMIN/VENDEDOR |

Para las rutas protegidas, envía `Authorization: Bearer <token>`. La tarjeta de usuarios del dashboard se obtiene de `/api/usuario/usuarios/count`, ya que pertenece a otro servicio. Las respuestas de los endpoints antiguos `/api/usuario/usuarios` y `/api/inventario` usan HATEOAS; los endpoints nuevos de catálogo y reposiciones devuelven JSON normal.

Ejemplo de login:

```json
{"correo":"admin@veterinaria.com","clave":"admin123"}
```

Ejemplo de reposición:

```json
{"productoId":1001,"cantidad":10}
```

Para recibirla, usa `PATCH /api/reposiciones/{id}/recibir`. La operación aumenta el stock y registra el movimiento en la misma transacción; no permite recibir dos veces la misma orden.

## Datos existentes y frontend

La migración Flyway de usuarios conserva una base que ya tenga la tabla `usuario` creada por Liquibase: Flyway marca el esquema existente como versión 1 y aplica las columnas nuevas de versión 2. Los usuarios anteriores siguen registrados, pero carecen de contraseña hasta que se establezca una; no pueden iniciar sesión todavía. No borres los datos ni la tabla de historial de Liquibase para hacer la transición.

El frontend del ZIP facilitado todavía lee y escribe `localStorage`. Para conectarlo, sustituye esas funciones por solicitudes al gateway y envía el JWT en operaciones administrativas. El backend no implementa checkout ni pagos, porque esa función aún no existe en la interfaz.
