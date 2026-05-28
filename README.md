# IoT Aforo App

## Informe del proyecto

Este proyecto es una aplicación de control de aforo IoT construida con Spring Boot, Spring Data JPA y una interfaz estática en HTML/CSS/JavaScript.

La aplicación soporta:
- Registro de entradas y salidas de personas en un negocio.
- Cálculo de aforo actual y estado de rentabilidad.
- Detección de sobreaforo con generación de alertas.
- Revisión de fin de día para detectar discrepancias entre entradas y salidas.
- Gestión de negocios y usuarios mediante un panel administrativo.
- Simulación de eventos de sensor IoT desde el frontend.
- Persistencia en memoria con base de datos H2.

## Archivos y componentes agregados

### Archivo principal
- `pom.xml`
  - Dependencias de Spring Boot:
    - `spring-boot-starter-webmvc`
    - `spring-boot-starter-data-jpa`
    - `spring-boot-h2console`
  - Base de datos en tiempo de ejecución: `com.h2database:h2`
  - Plugin: `spring-boot-maven-plugin`

### Configuración y arranque
- `src/main/java/com/iot/aforo/AforoApplication.java`
  - Clase principal de Spring Boot.
- `src/main/java/com/iot/aforo/DataSeeder.java`
  - Sembrador que crea:
    - Cuenta `MASTER_ADMIN` con correo `aleytochambi09@gmail.com` y contraseña `aleyto9044865535`.
    - Negocio de prueba `Tienda de Ropa Ronyc`.
    - Usuario `owner` con contraseña `owner123` y rol `USER`.

### Modelos de datos
- `src/main/java/com/iot/aforo/model/Business.java`
  - Entidad de negocio con aforo, capacidad y contadores de entradas/salidas.
- `src/main/java/com/iot/aforo/model/User.java`
  - Entidad de usuario con rol y asociación de negocio.
- `src/main/java/com/iot/aforo/model/Alert.java`
  - Entidad de alerta con marca de tiempo y estado de resolución.

### Repositorios
- `src/main/java/com/iot/aforo/repository/BusinessRepository.java`
- `src/main/java/com/iot/aforo/repository/UserRepository.java`
- `src/main/java/com/iot/aforo/repository/AlertRepository.java`

### Servicios
- `src/main/java/com/iot/aforo/service/BusinessService.java`
  - Lógica de negocio central:
    - Registrar entrada/salida.
    - Verificar sobreaforo y crear alertas.
    - Actualizar configuración de negocio.
    - Verificar fin de día y generar alertas si hay discrepancia.
    - Reiniciar contadores diarios.
- `src/main/java/com/iot/aforo/service/UserService.java`
  - Registro y validación de credenciales.
- `src/main/java/com/iot/aforo/service/AlertService.java`
  - Creación, listado y resolución de alertas.

### Controladores REST
- `src/main/java/com/iot/aforo/controller/AuthController.java`
  - Endpoint `/api/auth/login` para autenticación.
- `src/main/java/com/iot/aforo/controller/AdminController.java`
  - Endpoints `/api/admin/businesses` y `/api/admin/users`.
  - Soporta listado de negocios, creación de negocios y creación de usuarios.
- `src/main/java/com/iot/aforo/controller/BusinessController.java`
  - Endpoints `/api/business/{id}/status`, `/end-of-day`, `/alerts`, `/alerts/{alertId}/resolve`, `/reset`.
- `src/main/java/com/iot/aforo/controller/IoTController.java`
  - Endpoints `/api/iot/business/{id}/enter` y `/api/iot/business/{id}/exit`.

### Frontend estático
- `src/main/resources/static/index.html`
  - Vista de login y panel de control.
  - Panel dual para administradores y dueños de negocio.
  - Interfaz de simulación de eventos IoT.
- `src/main/resources/static/app.js`
  - Lógica de cliente para:
    - Autenticación.
    - Renderizado condicional de paneles según el rol.
    - Administración de negocios y usuarios.
    - Actualización de métricas de aforo.
    - Simulación de entradas y salidas.
- `src/main/resources/static/styles.css`
  - Estilos visuales de la aplicación.

## Uso del sistema

1. Ejecutar la aplicación con Maven:
   - En Windows: `mvnw.cmd spring-boot:run`
   - En Linux/Mac: `./mvnw spring-boot:run`
2. Acceder en el navegador a `http://localhost:8080`
3. Iniciar sesión con credenciales preconfiguradas.

## Cuentas de prueba

- `MASTER_ADMIN`
  - Usuario: `aleytochambi09@gmail.com`
  - Contraseña: `aleyto9044865535`
- `USER`
  - Usuario: `owner`
  - Contraseña: `owner123`

## Notas adicionales
- `HELP.md` contiene enlaces de referencia a Maven y Spring Boot.
- La base de datos H2 está disponible internamente y se recrea en cada ejecución.
- El sistema está diseñado como un prototipo de control de aforo y alerta para negocios.
