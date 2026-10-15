# Huerto urbano: microservicios de parcelas

Registro y consulta de parcelas (zonas de cultivo monitoreadas) con validación, sanitización y manejo de errores mediante excepciones y mensajes personalizados.

| Microservicio | Carpeta | Puerto | Tecnología |
|---|---|---|---|
| Base | `huerto_urbano/` | 8081 | Spring MVC, Spring Data JPA, PostgreSQL |
| Compose | `cmp_huerto_urbano/` | 9090 | Spring WebFlux, WebClient (consume el base) |

```text
Cliente → compose (9090) → base (8081) → PostgreSQL (5432, BD huerto_urbano)
```

## Requisitos

- JDK 21
- PostgreSQL 17 (o cualquier versión reciente) corriendo en `localhost:5432`
- Git Bash para usar los scripts `run.sh` (en Windows también funciona `mvnw.cmd`)

No hace falta instalar Maven: cada microservicio incluye el Maven Wrapper (`mvnw`).

## 1. Crear la base de datos

El microservicio base crea la tabla `parcel` al arrancar (`spring.jpa.hibernate.ddl-auto=update`), pero **la base de datos `huerto_urbano` debe existir antes**.

### Opción A: con `psql`

```bash
psql -U postgres -h localhost -c "CREATE DATABASE huerto_urbano;"
```

Pide la contraseña del usuario `postgres`. Si `psql` no se reconoce, agrega la carpeta `bin` de PostgreSQL al `PATH` (en Windows: `C:\Program Files\PostgreSQL\17\bin`) o usa la opción B.

### Opción B: con pgAdmin

1. Conectarse al servidor local.
2. Clic derecho en **Databases** → **Create** → **Database...**
3. En **Database** escribir `huerto_urbano` y dar **Save**.

### Comprobar que existe

```bash
psql -U postgres -h localhost -l
```

`huerto_urbano` debe aparecer en la lista.

## 2. Configurar la conexión

Las credenciales no se versionan (`application-dev.properties` está en `.gitignore`). Copiar la plantilla y poner el usuario y la contraseña de PostgreSQL:

```bash
cp huerto_urbano/src/main/resources/application-dev.properties.example huerto_urbano/src/main/resources/application-dev.properties
```

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/huerto_urbano
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

El compose no usa base de datos; la URL del base está en `cmp_huerto_urbano/src/main/resources/application.properties`.

## 3. Levantar los microservicios

Cada uno en su propia terminal, primero el base.

```bash
cd huerto_urbano
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

```bash
cd cmp_huerto_urbano
./mvnw spring-boot:run
```

Cada uno está listo cuando muestra `Started ...Application`. Si Maven está instalado, también se puede usar `./run.sh` en cada carpeta.

En PowerShell o CMD:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Los dos escuchan solo en `127.0.0.1`.

## 4. Endpoints

Los mismos en los dos microservicios, bajo `/api/v1/parcels`:

| Método | Ruta | Descripción | Respuesta correcta |
|---|---|---|---|
| `POST` | `/api/v1/parcels` | Registrar una parcela | 201 con `message` e `id` |
| `GET` | `/api/v1/parcels` | Listar las parcelas | 200 |
| `GET` | `/api/v1/parcels/{id}` | Consultar una parcela | 200 |

### Reglas de validación

| Campo | Regla |
|---|---|
| `name` | Obligatorio, de 3 a 100 caracteres, sin HTML, único |
| `latitude` | Obligatoria, de -90 a 90 |
| `longitude` | Obligatoria, de -180 a 180 |
| `soilType` | `ARCILLOSO`, `LIMOSO` o `ARENOSO` |
| `ownerOrLead` | Obligatorio, de 3 a 100 caracteres, sin HTML |

`id` y `createdAt` los asigna el base; si el cliente los envía, se ignoran.

### Errores

Todos responden con el mismo formato:

```json
{
  "message": "Los datos enviados no son válidos",
  "httpStatus": "400 BAD_REQUEST",
  "errors": { "name": "El nombre de la parcela contiene código HTML no permitido" }
}
```

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos (un mensaje por campo en `errors`), JSON mal formado o `id` no numérico |
| 404 | No existe la parcela (`ParcelNotFoundException`) |
| 409 | Ya existe una parcela con ese nombre (`DuplicateParcelException`) |
| 503 | Solo en el compose: el microservicio base no está disponible |

## 5. Probar

Contra el compose (`9090`); para probar el base directamente, usar el puerto `8081`.

Registrar (201). Si se repite, responde 409:

```bash
curl -i -X POST http://127.0.0.1:9090/api/v1/parcels -H "Content-Type: application/json" -d '{"name":"Parcela Norte","latitude":28.63,"longitude":-106.07,"soilType":"ARCILLOSO","ownerOrLead":"Ramiro Aragon"}'
```

Listar (200):

```bash
curl -i http://127.0.0.1:9090/api/v1/parcels
```

Consultar (200, o 404 con un id que no existe):

```bash
curl -i http://127.0.0.1:9090/api/v1/parcels/1
```

Sanitización (400 con el error en `name`):

```bash
curl -i -X POST http://127.0.0.1:9090/api/v1/parcels -H "Content-Type: application/json" -d '{"name":"<script>alert(1)</script>","latitude":28.63,"longitude":-106.07,"soilType":"ARCILLOSO","ownerOrLead":"Ramiro Aragon"}'
```

Comprobar en PostgreSQL:

```sql
SELECT * FROM parcel;
```
