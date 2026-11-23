# Bitácora Digital Académica (Android + Spring Boot)

Aplicación móvil nativa en **Kotlin + Jetpack Compose** con arquitectura **MVVM**, integrada a un backend de microservicios en **Spring Boot** con persistencia en base de datos **H2**, almacenamiento local en **Room** y consumo de **API externa**.

---

## Arquitectura y Tecnologías

### Aplicación Móvil (`/app`)
* **Lenguaje y UI:** Kotlin, Jetpack Compose (Material 3).
* **Arquitectura:** MVVM (`ViewModel`, `StateFlow`, `Repository`, `Domain Models` y validadores).
* **Persistencia Local:** Room Database (`BitacoraDatabase`, DAOs y Entities) con soporte offline y borradores.
* **Consumo de Servicios:** Retrofit 2 + Gson (`BitacoraApiService` para microservicios propios y `ExternalApiClient` para API externa).
* **Recursos Nativos:** Cámara (`TakePicture` con `FileProvider`), Galería (`PickVisualMedia`), `DatePickerDialog` y persistencia de estado ante rotación (`rememberSaveable`).
* **Pruebas Unitarias y Cobertura:** JUnit 4, MockK, `kotlinx-coroutines-test` y Kover (`app/src/test/`).

### Backend Microservicios (`/backend`)
* **Framework:** Spring Boot (Java) con arquitectura en capas (`Controller`, `Repository`, `Model`).
* **Base de Datos:** H2 embebida persistente en disco (`./data/bitacoradb`).
* **Endpoints Principales:**
  * `POST /api/usuarios/registro` · `POST /api/usuarios/login`
  * `GET /api/cursos` · `POST /api/cursos` (regla de negocio: máximo 2 cursos por año)
  * `GET /api/bitacoras` · `POST /api/bitacoras` · `PUT /api/bitacoras/{id}/publicar`
  * `GET /api/entradas/estudiante/{id}` · `GET /api/entradas/enviadas` · `POST /api/entradas` · `PUT /api/entradas/{id}/revisar`

### API Externa (`IE 3.1.4`)
* **Servicio:** API pública de Feriados de Chile (`https://api.boostr.cl/holidays.json`).
* **Implementación:** Cliente Retrofit independiente (`ExternalApiClient` / `FeriadosRepository`) que consulta el próximo feriado académico sin interferir con los microservicios propios.

---

## Ejecución del Proyecto

### 1. Levantar Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
```
El servidor inicia en `http://localhost:8080` (accesible desde el emulador Android vía `http://10.0.2.2:8080/`).

### 2. Ejecutar Pruebas Unitarias
```bash
./gradlew test
```

### 3. Generar APK Firmado (`Release`)
El proyecto incluye la llave `app/bitacora-release.jks` y el bloque `signingConfigs` en `app/build.gradle.kts`:
```bash
./gradlew assembleRelease
```
El APK firmado se genera en `app/build/outputs/apk/release/app-release.apk`.