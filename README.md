# ms-banco (Microservicio)

Microservicio `ms-banco` que expone APIs para gestionar clientes, cuentas y movimientos, y genera reportes de estado de cuenta (JSON y PDF en Base64).

Base URL local: http://localhost:8081/

Frontend que consume este backend: https://github.com/JoelOntuDeveloper/banco-app

**Requisitos**
- Java 21
- Gradle (se usa el wrapper `gradlew` / `gradlew.bat`)
- Base de datos MySQL (configurable en `src/main/resources/application.properties`)

**Notas técnicas**
- El servicio utiliza la librería OpenPDF para generar PDFs en memoria y retorna el contenido en Base64 para que el frontend lo decodifique.
- La base de datos se encuentra en la raíz del proyecto [BaseDatos.sql](/BaseDatos.sql)
- La colección de **POSTMAN** también se encuentra en la razí del proyecto [Banco Api Rest.postman_collection](/Banco%20Api%20Rest.postman_collection.json)


**Build y ejecución**

En Windows (desde la raíz del proyecto):

```powershell
.\gradlew.bat clean build
.\gradlew.bat bootRun
```

O bien ejecutar el JAR generado:

```powershell
java -jar build\libs\ms-banco-1.0.0-SNAPSHOT.jar
```

**Configuración**
- Puerto por defecto: `8081` (ver `src/main/resources/application.properties`).
- Datos de conexión a la base de datos en [application.properties](/src/main/resources/application.properties).

**Despliegue en Docker**

El proyecto incluye `Dockerfile` y `docker-compose.yml` para desplegar en contenedores.

Con Docker Compose:

```bash
docker-compose up --build
```

Acceder a la aplicación en http://localhost:8081/

**Endpoints relevantes**
- `GET /api/reportes/{clienteId}?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD`
	- Retorna el reporte (`EstadoCuentaReporteDTO`) en JSON con cuentas y movimientos.

- `GET /api/reportes/{clienteId}/pdf?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD`
	- Retorna un `FileBase64DTO` con el PDF del estado de cuenta codificado en Base64.
	- `FileBase64DTO` contiene: `fileName`, `fileType` (`application/pdf`) y `base64Content`.

**Ejemplos (curl)**

Obtener reporte JSON:

```bash
curl "http://localhost:8081/api/reportes/1?fechaInicio=2025-01-01&fechaFin=2025-12-31"
```

Obtener PDF en Base64:

```bash
curl "http://localhost:8081/api/reportes/1/pdf?fechaInicio=2025-01-01&fechaFin=2025-12-31"
```