# 📊 IoT Aforo App - Documentación Completa del Proyecto

## 🎯 Descripción General del Proyecto

**IoT Aforo App** es un sistema integral de **control de aforo y rentabilidad para negocios** construido con **Spring Boot 4.0.6** y **tecnologías IoT**. El sistema permite monitorizar en tiempo real la ocupación de establecimientos, detectar sobreaforo, gestionar alertas y verificar la rentabilidad operativa de los negocios.

### Propósito Principal
- Registrar entradas y salidas de personas en establecimientos
- Monitorizar la capacidad actual y detectar excedentes
- Generar alertas automáticas cuando se superan límites
- Verificar discrepancias entre entradas y salidas al final del día
- Gestionar múltiples negocios desde un panel administrativo centralizado
- Simular eventos de sensores IoT desde la interfaz web

### Casos de Uso
1. **Tiendas minoristas**: Control de capacidad en horarios de confinamiento
2. **Restaurantes/Cafeterías**: Gestión de mesas y capacidad
3. **Cines/Teatros**: Monitoreo de asientos disponibles
4. **Eventos**: Control de asistencia en tiempo real
5. **Edificios comerciales**: Monitoreo de ocupación por piso

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 21**: Lenguaje de programación principal
- **Spring Boot 4.0.6**: Framework web REST
- **Spring Data JPA**: ORM para persistencia de datos
- **Hibernate**: Mapeo objeto-relacional con DDL automático (update)
- **H2 Database**: Base de datos en memoria para desarrollo/testing
- **Maven**: Gestor de dependencias y build

### Frontend
- **HTML5**: Estructura semántica
- **CSS3**: Diseño responsivo con CSS Grid y Glassmorphism
- **JavaScript (Vanilla)**: Lógica frontend sin dependencias externas
- **Google Fonts (Outfit)**: Tipografía moderna

### Infraestructura
- **Puerto de ejecución**: 8080
- **URL base**: `http://localhost:8080`
- **Consola H2**: `http://localhost:8080/h2-console`

---

## 🏗️ Arquitectura del Sistema

### Patrón de Diseño: MVC + Capas

```
┌─────────────────────────────────────────────────────────────┐
│                         FRONTEND                            │
│              (HTML/CSS/JavaScript - Static)                 │
│  ├─ index.html (Vista única con vistas por rol)             │
│  ├─ app.js (Lógica de aplicación y API calls)              │
│  └─ styles.css (Diseño responsivo)                          │
└──────────────────────┬──────────────────────────────────────┘
                       │ REST API (JSON)
┌──────────────────────▼──────────────────────────────────────┐
│                    CONTROLLERS LAYER                        │
│  ├─ AuthController (POST /api/auth/login)                   │
│  ├─ BusinessController (GET/POST /api/business/*)           │
│  ├─ IoTController (POST /api/iot/business/*/enter|exit)     │
│  └─ AdminController (GET/POST /api/admin/*)                 │
└──────────────────────┬──────────────────────────────────────┘
                       │ Service Pattern
┌──────────────────────▼──────────────────────────────────────┐
│                     SERVICES LAYER                          │
│  ├─ BusinessService (Lógica de negocios y aforo)            │
│  ├─ UserService (Gestión de usuarios)                       │
│  └─ AlertService (Sistema de alertas)                       │
└──────────────────────┬──────────────────────────────────────┘
                       │ Repository Pattern
┌──────────────────────▼──────────────────────────────────────┐
│                   REPOSITORIES LAYER                        │
│  ├─ BusinessRepository (CRUD de Business)                   │
│  ├─ UserRepository (CRUD de User + findByUsername)          │
│  └─ AlertRepository (CRUD de Alert + queries específicas)   │
└──────────────────────┬──────────────────────────────────────┘
                       │ JPA/Hibernate
┌──────────────────────▼──────────────────────────────────────┐
│                    MODEL LAYER (JPA)                        │
│  ├─ Business (@Entity)                                      │
│  ├─ User (@Entity)                                          │
│  └─ Alert (@Entity)                                         │
└──────────────────────┬──────────────────────────────────────┘
                       │ SQL
┌──────────────────────▼──────────────────────────────────────┐
│              H2 DATABASE (In-Memory)                        │
│  ├─ businesses (table)                                      │
│  ├─ users (table)                                           │
│  └─ alerts (table)                                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Estructura del Proyecto

```
IoT-aforo-app-02/
├── pom.xml                                  # Configuración Maven
├── mvnw / mvnw.cmd                          # Maven wrapper (Windows)
├── README.md                                # Descripción inicial
├── documentacion/
│   └── README.md                            # 👈 ESTE ARCHIVO
├── src/
│   ├── main/
│   │   ├── java/com/iot/aforo/
│   │   │   ├── AforoApplication.java        # ⭐ Clase principal Spring Boot
│   │   │   ├── DataSeeder.java              # 🌱 Inicialización de datos
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java      # Login & autenticación
│   │   │   │   ├── BusinessController.java  # Gestión de negocios
│   │   │   │   ├── IoTController.java       # Eventos de sensores IoT
│   │   │   │   └── AdminController.java     # Panel administrativo
│   │   │   ├── model/
│   │   │   │   ├── Business.java            # Entidad de negocio
│   │   │   │   ├── User.java                # Entidad de usuario
│   │   │   │   └── Alert.java               # Entidad de alerta
│   │   │   ├── repository/
│   │   │   │   ├── BusinessRepository.java  # Acceso a negocios
│   │   │   │   ├── UserRepository.java      # Acceso a usuarios
│   │   │   │   └── AlertRepository.java     # Acceso a alertas
│   │   │   └── service/
│   │   │       ├── BusinessService.java     # Lógica de negocio
│   │   │       ├── UserService.java         # Gestión de usuarios
│   │   │       └── AlertService.java        # Gestión de alertas
│   │   └── resources/
│   │       ├── application.properties       # Configuración Spring
│   │       └── static/
│   │           ├── index.html               # Interfaz web
│   │           ├── app.js                   # Lógica frontend
│   │           └── styles.css               # Estilos
│   └── test/
│       └── java/com/iot/aforo/
│           └── AforoApplicationTests.java   # Tests unitarios
└── target/                                  # Build output (compilado)
```

---

## 🗄️ Estructura de Datos

### 1. Tabla: `businesses`
Representa los establecimientos gestionados en el sistema.

```sql
CREATE TABLE businesses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    max_capacity INT NOT NULL,           -- Aforo máximo permitido
    min_capacity_profit INT NOT NULL,    -- Ocupación mínima para rentabilidad
    department VARCHAR(255),             -- Departamento (provincia) ubicación
    current_count INT DEFAULT 0,         -- Personas dentro actualmente
    total_entries_today INT DEFAULT 0,   -- Entradas del día
    total_exits_today INT DEFAULT 0      -- Salidas del día
);
```

**Campos Clave:**
- `current_count`: Se incrementa con `/api/iot/enter` y decrementa con `/api/iot/exit`
- `totalEntriesToday`: Solo se incrementa, nunca decrece
- `totalExitsToday`: Solo se incrementa, nunca decrece
- `minCapacityProfit`: Umbral para considerar el negocio rentable

### 2. Tabla: `users`
Gestiona usuarios con roles jerárquicos del sistema.

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,           -- MASTER_ADMIN, ADMIN, USER
    business_id BIGINT                   -- FK a businesses (solo para USER)
);
```

**Roles:**
- `MASTER_ADMIN`: Control total del sistema, puede crear ADMINs
- `ADMIN`: Administrador subordinado, puede crear USERs
- `USER`: Dueño de negocio, asociado a exactamente UN negocio

### 3. Tabla: `alerts`
Sistema de alertas para eventos críticos.

```sql
CREATE TABLE alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_id BIGINT NOT NULL,         -- FK a businesses
    message VARCHAR(1000) NOT NULL,      -- Descripción del evento
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Cuándo ocurrió
    resolved BOOLEAN DEFAULT FALSE       -- Si ya fue revisada
);
```

**Tipos de Alertas Generadas:**
1. **Alerta de Capacidad**: `currentCount > maxCapacity`
2. **Alerta de Fin de Día**: `totalEntries ≠ totalExits`

---

## 👥 Modelos de Datos (Código Java)

### Business.java
```java
@Entity
@Table(name = "businesses")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;                    // Nombre del negocio
    private int maxCapacity;                // Aforo máximo
    private int minCapacityProfit;          // Ocupación mínima rentable
    private String department;              // Ubicación
    private int currentCount;               // Conteo actual
    private int totalEntriesToday;          // Acumulado entradas
    private int totalExitsToday;            // Acumulado salidas
    
    // Métodos lógica
    public void incrementEntries() {
        this.currentCount++;
        this.totalEntriesToday++;
    }
    
    public void incrementExits() {
        if (this.currentCount > 0) {
            this.currentCount--;
        }
        this.totalExitsToday++;
    }
}
```

### User.java
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;               // Email o nombre único
    private String password;               // Contraseña (⚠️ sin hash en esta versión)
    private String role;                   // MASTER_ADMIN, ADMIN, USER
    private Long businessId;               // Asociación a negocio
}
```

### Alert.java
```java
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long businessId;               // Negocio afectado
    private String message;                // Descripción de la alerta
    private LocalDateTime timestamp;       // Marca de tiempo
    private boolean resolved;              // Si ya fue resuelta
}
```

---

## 🔐 Sistema de Autenticación y Autorización

### Flujo de Login

```
1. Usuario ingresa credenciales (username, password) en index.html
2. Frontend hace POST a /api/auth/login
3. AuthController valida las credenciales:
   - Busca el usuario por username
   - Compara password exactamente (⚠️ sin encriptación)
4. Si valido:
   - Retorna JSON: { id, username, role, businessId }
   - Frontend guarda en localStorage.aforo_user
5. Si inválido:
   - Retorna 401 UNAUTHORIZED
```

### Jerarquía de Roles

```
MASTER_ADMIN (Nivel 3)
    ├─ Acceso completo al sistema
    ├─ Puede crear ADMIN y USER
    ├─ Visualiza todos los negocios
    └─ Acceso a panel administrativo completo

ADMIN (Nivel 2)
    ├─ Subordinado del MASTER_ADMIN
    ├─ Puede crear USERs
    ├─ Visualiza todos los negocios
    ├─ NO puede crear otros ADMINs
    └─ Acceso a panel administrativo

USER (Nivel 1)
    ├─ Dueño de negocio
    ├─ Asociado a UN negocio específico
    ├─ NO puede crear usuarios
    ├─ Solo visualiza SU negocio
    └─ Acceso al monitoreo en tiempo real
```

### Reglas de Negocio (AdminController.java)

```java
// Solo MASTER_ADMIN puede crear cuentas de tipo ADMIN
if ("ADMIN".equals(roleRequested) && !"MASTER_ADMIN".equals(creator.getRole())) {
    return ResponseEntity.status(403).body("Solo la cuenta maestra puede crear otros administradores.");
}

// Nadie puede crear otro MASTER_ADMIN
if ("MASTER_ADMIN".equals(roleRequested)) {
    return ResponseEntity.status(403).body("No se puede crear otra cuenta maestra.");
}

// Un USER no puede crear a nadie
if ("USER".equals(creator.getRole())) {
    return ResponseEntity.status(403).body("Los dueños de negocio no tienen permiso para crear cuentas.");
}
```

---

## 📡 API Endpoints

### 1. Autenticación

#### POST `/api/auth/login`
Autentica un usuario y retorna su información.

**Request:**
```json
{
    "username": "owner",
    "password": "owner123"
}
```

**Response (200 OK):**
```json
{
    "id": 2,
    "username": "owner",
    "role": "USER",
    "businessId": 1
}
```

**Error (401):**
```json
"Credenciales incorrectas."
```

---

### 2. Negocio - Endpoints IoT

#### POST `/api/iot/business/{id}/enter`
Registra que una persona **entra** al negocio.

**Parámetros:**
- `{id}`: ID del negocio

**Response (200 OK):**
```json
{
    "id": 1,
    "name": "Tienda de Ropa Ronyc",
    "currentCount": 5,
    "totalEntriesToday": 5,
    "totalExitsToday": 0,
    "maxCapacity": 10,
    "minCapacityProfit": 3
}
```

**Lógica:**
- Incrementa `currentCount` en 1
- Incrementa `totalEntriesToday` en 1
- Si `currentCount > maxCapacity` → Genera alerta

#### POST `/api/iot/business/{id}/exit`
Registra que una persona **sale** del negocio.

**Response (200 OK):**
```json
{
    "id": 1,
    "currentCount": 4,
    "totalExitsToday": 1,
    ...
}
```

**Lógica:**
- Decrementa `currentCount` en 1 (mínimo 0)
- Incrementa `totalExitsToday` en 1

---

### 3. Negocio - Endpoints de Estado

#### GET `/api/business/{id}/status`
Obtiene estado actual del negocio con métricas.

**Response (200 OK):**
```json
{
    "businessId": 1,
    "name": "Tienda de Ropa Ronyc",
    "currentCount": 5,
    "maxCapacity": 10,
    "minCapacityProfit": 3,
    "totalEntriesToday": 5,
    "totalExitsToday": 0,
    "meetsProfitability": true,
    "isFull": false
}
```

#### POST `/api/business/{id}/end-of-day`
Ejecuta la verificación de fin de día.

**Response (200 OK):**
```json
{
    "match": true,
    "totalEntriesToday": 10,
    "totalExitsToday": 10,
    "message": "El conteo de entradas y salidas coincide perfectamente. ¡Buen trabajo!"
}
```

**Si NO coinciden (match: false):**
- Genera alerta automáticamente
- Retorna mensaje de discrepancia

#### POST `/api/business/{id}/reset`
Reinicia los contadores diarios a cero.

**Response (200 OK):**
```json
"Conteos diarios reiniciados con éxito."
```

**Campos reseteados:**
- `currentCount = 0`
- `totalEntriesToday = 0`
- `totalExitsToday = 0`

---

### 4. Alertas

#### GET `/api/business/{id}/alerts`
Lista todas las alertas activas del negocio.

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "businessId": 1,
        "message": "¡ALERTA DE CAPACIDAD! El negocio 'Tienda de Ropa Ronyc' ha superado el aforo máximo. Personas adentro: 11, Capacidad límite: 10.",
        "timestamp": "2024-06-03T14:30:25.123456",
        "resolved": false
    }
]
```

#### PUT `/api/business/{id}/alerts/{alertId}/resolve`
Marca una alerta como resuelta.

**Response (200 OK):**
```json
{
    "id": 1,
    "resolved": true,
    ...
}
```

---

### 5. Administración - Negocio

#### GET `/api/admin/businesses`
Lista TODOS los negocios (solo ADMINs).

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "Tienda de Ropa Ronyc",
        "maxCapacity": 10,
        "minCapacityProfit": 3,
        "department": "Lima",
        "currentCount": 5,
        "totalEntriesToday": 5,
        "totalExitsToday": 0
    }
]
```

#### POST `/api/admin/businesses`
Crea un nuevo negocio (solo ADMINs).

**Request:**
```json
{
    "name": "Restaurante La Buena Sazón",
    "maxCapacity": 30,
    "minCapacityProfit": 10,
    "department": "Arequipa"
}
```

**Response (200 OK):**
```json
{
    "id": 2,
    "name": "Restaurante La Buena Sazón",
    "maxCapacity": 30,
    "minCapacityProfit": 10,
    ...
}
```

#### PUT `/api/admin/businesses/{id}/config`
Actualiza aforo y ganancias mínimas.

**Request:**
```json
{
    "maxCapacity": 20,
    "minCapacityProfit": 8
}
```

**Response (200 OK):**
```json
{
    "id": 1,
    "maxCapacity": 20,
    "minCapacityProfit": 8,
    ...
}
```

---

### 6. Administración - Usuarios

#### POST `/api/admin/users`
Crea un nuevo usuario (requiere autenticación).

**Request:**
```json
{
    "username": "dueño.tienda@example.com",
    "password": "password123",
    "role": "USER",
    "businessId": "1"
}
```

**Headers Requeridos:**
```
X-Creator-Id: 1   (ID del usuario que crea)
```

**Response (200 OK):**
```json
{
    "id": 3,
    "username": "dueño.tienda@example.com",
    "role": "USER",
    "businessId": 1
}
```

**Errores Posibles (400/403):**
- `"Los dueños de negocio no tienen permiso para crear cuentas."` (USER intenta crear)
- `"Solo la cuenta maestra (Master Admin) puede crear otros administradores."` (ADMIN intenta crear ADMIN)
- `"No se puede crear otra cuenta maestra."` (Intento de crear MASTER_ADMIN)

---

## 🎯 Servicios de Negocio

### BusinessService

```java
@Service
public class BusinessService {
    
    // Obtiene negocio por ID o lanza excepción
    public Business getBusiness(Long id)
    
    // Crea negocio con parámetros
    public Business createBusiness(String name, int maxCapacity, int minCapacityProfit, String department)
    
    // Registra entrada y genera alerta si se supera aforo
    @Transactional
    public Business registerEntry(Long businessId)
    
    // Registra salida (nunca baja de 0)
    @Transactional
    public Business registerExit(Long businessId)
    
    // Actualiza configuración de aforo
    @Transactional
    public Business updateConfig(Long businessId, int maxCapacity, int minCapacityProfit)
    
    // Verifica discrepancias de fin de día
    @Transactional
    public boolean performEndOfDayCheck(Long businessId)
    
    // Reinicia contadores del día
    @Transactional
    public void resetDailyCounts(Long businessId)
}
```

### UserService

```java
@Service
public class UserService {
    
    // Busca usuario por nombre/email
    public Optional<User> findByUsername(String username)
    
    // Busca usuario por ID
    public Optional<User> findById(Long id)
    
    // Crea usuario (lanza excepción si ya existe)
    public User createUser(String username, String password, String role, Long businessId)
    
    // Valida credenciales (comparación directa de password)
    public boolean validateCredentials(String username, String password)
}
```

### AlertService

```java
@Service
public class AlertService {
    
    // Crea alerta inmediatamente
    public Alert createAlert(Long businessId, String message)
    
    // Obtiene TODAS las alertas de un negocio
    public List<Alert> getAlertsForBusiness(Long businessId)
    
    // Obtiene solo alertas NO resueltas
    public List<Alert> getActiveAlertsForBusiness(Long businessId)
    
    // Marca alerta como resuelta
    public Alert resolveAlert(Long alertId)
}
```

---

## 🎮 Interfaz de Usuario (Frontend)

### Vistas por Rol

#### 1. Vista de Login (Pública)
```
┌─────────────────────────────┐
│       AforoIoT              │  (Logo con animación)
│  Prototipo de Control...    │
│                             │
│  [Usuario/Correo]           │
│  [Contraseña]               │
│  [Iniciar Sesión]           │
│                             │
│  Credenciales en walkthrough│
└─────────────────────────────┘
```

#### 2. Vista MASTER_ADMIN / ADMIN
**Panel Administrativo:**

**Sección 1: Gestión de Negocios**
- Formulario para crear nuevo negocio
- Campo: Nombre, Aforo Máx, Mínimo Rentable, Departamento
- Tabla listando negocios con opción "Configurar"
- Modal para editar parámetros de negocio

**Sección 2: Gestión de Usuarios**
- Formulario para crear usuarios
- Campos: Username, Password, Rol, Negocio (si aplica)
- Validaciones en cliente y servidor
- Mensajes de éxito/error

**Diferenciales MASTER_ADMIN:**
- Opción para crear ADMINs (invisible en ADMIN)
- Total control sin restricciones

#### 3. Vista USER (Dueño de Negocio)
**Panel de Monitoreo en Tiempo Real:**

**Círculo de Aforo (Central):**
```
    ┌─────────────────────┐
    │    PERSONAS         │
    │        5            │
    │    ADENTRO          │
    └─────────────────────┘
    [Barra de progreso: 50%]
```

**Métricas Clave:**
- Aforo Máximo: 10
- Mínimo Rentable: 3
- Entradas Hoy: 5
- Salidas Hoy: 0
- Rentabilidad: ✓ CUMPLIDA / ✗ NO CUMPLIDA
- Capacidad: [LLENO / EXCEDIDO] (si aplica)

**Botones de Simulación:**
- [+ Persona Entra] → POST /api/iot/business/{id}/enter
- [- Persona Sale] → POST /api/iot/business/{id}/exit
- [🔄 Cierre Fin de Día] → POST /api/business/{id}/end-of-day
- [🔁 Reiniciar Contadores] → POST /api/business/{id}/reset

**Sistema de Alertas (en tiempo real):**
```
┌─────────────────────────────────┐
│ ⚠️ ¡ALERTA DE CAPACIDAD!         │
│ Superado: 11/10 personas        │ 14:30:25
│                    [Resolver]   │
└─────────────────────────────────┘
```

**Actualización en Tiempo Real:**
- Polling cada 3 segundos (línea 511 en app.js)
- Renderiza automáticamente nuevos datos
- Detecta cambios de color (Normal → Warning → Danger)

---

## 🌱 Inicialización de Datos (DataSeeder)

Se ejecuta automáticamente al iniciar la aplicación:

```java
@Component
public class DataSeeder implements CommandLineRunner {
    
    // Si la base de datos está vacía, crea:
    
    1. Cuenta MASTER_ADMIN
       - username: "aleytochambi09@gmail.com"
       - password: "aleyto9044865535"
       - role: "MASTER_ADMIN"
    
    2. Negocio de prueba
       - name: "Tienda de Ropa Ronyc"
       - maxCapacity: 10
       - minCapacityProfit: 3
       - department: "Lima"
    
    3. Usuario de prueba (Usuario tipo)
       - username: "owner"
       - password: "owner123"
       - role: "USER"
       - businessId: <ID del negocio>
}
```

**Resultado en Consola:**
```
>>> Sembrador: Cuenta Maestra MASTER_ADMIN creada (aleytochambi09@gmail.com)
>>> Sembrador: Negocio de prueba creado: Tienda de Ropa Ronyc (ID: 1)
>>> Sembrador: Dueño de negocio creado por defecto (owner / owner123)
```

---

## ⚙️ Configuración (application.properties)

```properties
# Nombre de la aplicación
spring.application.name=aforo

# Puerto HTTP
server.port=8080

# Base de datos H2 (en memoria)
spring.datasource.url=jdbc:h2:mem:aforodb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Dialecto Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Consola H2 (acceso web a la BD)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true

# DDL automático (crear/actualizar tablas)
spring.jpa.hibernate.ddl-auto=update

# Log de SQL (útil para debugging)
spring.jpa.show-sql=true
```

**URL Consola H2:** `http://localhost:8080/h2-console`

---

## 🚀 Guía de Instalación y Ejecución

### Requisitos Previos
- **Java 21+** instalado (verificar: `java -version`)
- **Maven** instalado O usar `mvnw.cmd` (incluido)
- **Git** (para clonar el repositorio)

### Pasos de Instalación

**1. Clonar el repositorio**
```bash
git clone <URL-del-repositorio>
cd IoT-aforo-app-02
```

**2. Compilar el proyecto con Maven**
```bash
# En Windows, usando Maven wrapper
mvnw.cmd clean package

# O si tienes Maven instalado globalmente
mvn clean package
```

**3. Ejecutar la aplicación**
```bash
# Opción A: Usando Maven
mvn spring-boot:run

# Opción B: Ejecutar JAR directamente
java -jar target/aforo-0.0.1-SNAPSHOT.jar
```

**4. Verificar que inició correctamente**
- Ve a `http://localhost:8080`
- Deberías ver la pantalla de login

### Credenciales de Prueba Iniciales

| Usuario | Contraseña | Rol | Acceso |
|---------|-----------|------|--------|
| `aleytochambi09@gmail.com` | `aleyto9044865535` | MASTER_ADMIN | Panel admin completo |
| `owner` | `owner123` | USER | Monitoreo de "Tienda de Ropa Ronyc" |

---

## 🔍 Acceso a la Base de Datos (H2)

**Consola Web H2:**
- URL: `http://localhost:8080/h2-console`
- Driver: `org.h2.Driver`
- URL JDBC: `jdbc:h2:mem:aforodb`
- Usuario: `sa`
- Contraseña: (dejar en blanco)

**Queries Útiles:**
```sql
-- Ver todos los negocios
SELECT * FROM businesses;

-- Ver todos los usuarios
SELECT * FROM users;

-- Ver alertas activas
SELECT * FROM alerts WHERE resolved = FALSE;

-- Ver aforo actual de un negocio
SELECT id, name, current_count, max_capacity FROM businesses WHERE id = 1;

-- Ver discrepancias de día
SELECT name, total_entries_today, total_exits_today, (total_entries_today - total_exits_today) AS discrepancia 
FROM businesses;
```

---

## 🧪 Flujo de Prueba Manual

### Escenario 1: Monitoreo de Capacidad

1. Loguearse como `owner` / `owner123`
2. Ver panel con Tienda de Ropa Ronyc
3. Hacer clic en **[+ Persona Entra]** 5 veces
4. Observar:
   - `currentCount` = 5
   - `totalEntriesToday` = 5
   - Barra de progreso = 50%
   - Rentabilidad: CUMPLIDA ✓
5. Hacer clic **[+ Persona Entra]** 5 veces más
6. Ahora:
   - `currentCount` = 10
   - Barra = 100%
   - Capacidad: LLENO
7. Hacer clic una vez más: `currentCount` = 11
   - Barra roja al 110%
   - Alerta generada automáticamente
   - Alerta visible en panel de alertas

### Escenario 2: Discrepancia de Fin de Día

1. Entradas actuales: 11, Salidas: 0
2. Hacer clic [- Persona Sale] 10 veces
3. Ahora: Entradas: 11, Salidas: 10
4. Hacer clic [🔄 Cierre Fin de Día]
5. Mensaje: "Discrepancia detectada. Se ha generado una alerta para revisión."
6. Se ve alerta en el listado

### Escenario 3: Administración (como MASTER_ADMIN)

1. Loguearse como `aleytochambi09@gmail.com` / `aleyto9044865535`
2. Ver Panel de Control - Master Admin
3. **Crear Negocio:**
   - Nombre: "Cine Multiplaza"
   - Aforo Máx: 200
   - Mín. Rentable: 50
   - Depto: "Cusco"
   - Clic [Registrar Negocio]
4. Negocio aparece en tabla
5. **Crear Usuario:**
   - Username: `gerente.cine@example.com`
   - Password: `pass123456`
   - Rol: USER
   - Negocio: "Cine Multiplaza"
   - Clic [Crear Cuenta]
6. Mensaje de éxito
7. Prueba logout y login con nueva cuenta

---

## 🔄 Flujo de Datos: Ejemplo Completo

```
USUARIO: owner (USER) logueado en "Tienda de Ropa Ronyc"

PASO 1: Sensor IoT detecta persona entrando
  └─> Triggering: /api/iot/business/1/enter (POST)

PASO 2: IoTController recibe petición
  └─> Llama: businessService.registerEntry(1)

PASO 3: BusinessService procesa
  ├─> Obtiene Business con id=1
  ├─> Ejecuta: business.incrementEntries()
  │   ├─> currentCount: 0 → 1
  │   ├─> totalEntriesToday: 0 → 1
  ├─> Verifica: ¿1 > 10? NO
  └─> Guarda cambios en BD

PASO 4: Respuesta va a frontend
  └─> app.js actualiza interfaz

PASO 5: Polling cada 3 seg detecta cambio
  └─> GET /api/business/1/status
  └─> Renderiza nuevos datos en tiempo real
```

**Ahora si currentCount llegara a 11:**

```
PASO 3B: BusinessService detecta sobreaforo
  ├─> Verifica: ¿11 > 10? SÍ
  ├─> Llama: alertService.createAlert(1, "¡ALERTA DE CAPACIDAD!...")
  │   └─> Crea fila en tabla alerts (resolved = false)
  └─> Guarda Business cambios

PASO 5B: Frontend ve alerta en polling
  └─> GET /api/business/1/alerts
  └─> Renderiza alerta en UI roja
  └─> Usuario ve [⚠️ ALERTA | Resolver]
```

---

## 📊 Características Principales

### ✅ Funcionalidades Implementadas

1. **Autenticación Multi-Rol**
   - Login seguro con roles jerárquicos
   - Almacenamiento en localStorage
   - Validación servidor-lado

2. **Monitoreo de Aforo en Tiempo Real**
   - Registro de entradas/salidas
   - Cálculo de ocupación actual
   - Indicadores visuales (barra de progreso, colores dinámicos)

3. **Sistema de Alertas Inteligentes**
   - Alerta automática por sobreaforo
   - Alerta por discrepancia de fin de día
   - Resolución manual de alertas
   - Marca de tiempo en cada alerta

4. **Gestión Administrativa Centralizada**
   - Panel para crear negocios
   - Panel para crear usuarios
   - Configuración de parámetros por negocio
   - Control de roles jerárquico

5. **Verificación de Fin de Día**
   - Comparación entradas vs salidas
   - Generación automática de reportes
   - Reinicio de contadores

6. **Simulación de Sensores IoT**
   - Botones para simular entrada/salida
   - Ideal para testing sin dispositivos reales
   - Compatible con futuros sensores reales

### 🚀 Características Futuras Recomendadas

1. **Seguridad Mejorada**
   - Encriptación de contraseñas (bcrypt)
   - JWT tokens en lugar de localStorage plano
   - HTTPS enforcement

2. **Datos Persistentes**
   - Cambiar de H2 en memoria a PostgreSQL/MySQL
   - Historial permanente de alertas
   - Reportes históricos

3. **Integración IoT Real**
   - Sensores de conteo real (cámaras + IA)
   - API para webhooks de sensores
   - Soporte para múltiples tipos de sensores

4. **Análisis y Reportes**
   - Estadísticas por hora/día/semana
   - Gráficos de ocupación
   - Predicción de tendencias

5. **Notificaciones**
   - Email para alertas críticas
   - SMS a administradores
   - Notificaciones push

6. **Multi-Language**
   - Soporte para idiomas adicionales
   - Interfaz internacionalizada

---

## 🐛 Troubleshooting

### Error: "No tienes permiso para crear ADMINs"
- **Causa**: Intentaste crear un ADMIN siendo ADMIN (no MASTER_ADMIN)
- **Solución**: Logueate como MASTER_ADMIN

### Error: "El usuario ya existe"
- **Causa**: Username duplicado en BD
- **Solución**: Usa un username único

### Error: "Debe asociar la cuenta a un negocio para el rol de Dueño de Negocio"
- **Causa**: Intentaste crear USER sin seleccionar negocio
- **Solución**: Elige un negocio del dropdown

### Interfaz en blanco después de login
- **Causa**: Posible error de CORS
- **Solución**: Verifica consola browser (F12) para errores
- Asegúrate que Spring Boot está en http://localhost:8080

### Contadores no actualizan
- **Causa**: Polling podría estar pausado
- **Solución**: Abre consola, verifica si hay errores JavaScript
- Recarga la página

### H2 Console no accesible
- **Causa**: `spring.h2.console.enabled` está false
- **Solución**: En `application.properties`, asegúrate que sea `true`

---

## 📝 Notas de Desarrollo

### Decisiones Arquitectónicas

1. **H2 en Memoria**
   - Ventaja: Fácil setup, ideal para desarrollo
   - Desventaja: Datos se pierden al reiniciar
   - Recomendación: Cambiar a PostgreSQL en producción

2. **Contraseñas sin Encriptación**
   - ⚠️ INSEGURO para producción
   - Recomendación: Implementar BCrypt

3. **localStorage para Autenticación**
   - Funciona pero vulnerable a XSS
   - Recomendación: JWT + HttpOnly cookies

4. **Polling en lugar de WebSockets**
   - Simpler de implementar
   - Más latencia
   - Suficiente para prototipo

5. **Sin ORM Cache**
   - Cada petición va a BD
   - OK para volumen bajo

### Consideraciones de Performance

- Máx 10-20 negocios simultáneos recomendado (con H2)
- Máx 50-100 alertas por negocio antes de limpiar
- Polling cada 3 segundos es moderado
- Para producción: implementar paginación en alertas

---

## 👨‍💻 Contribución

Para que otros programadores trabajen con este código:

1. **Clone el repo**: `git clone ...`
2. **Branch feature**: `git checkout -b feature/mi-funcionalidad`
3. **Haz cambios** siguiendo esta estructura
4. **Test localmente**: `mvn test`
5. **Commit descriptivo**: `git commit -m "feat: agregar ..."`
6. **Push**: `git push origin feature/mi-funcionalidad`
7. **Pull Request**: Describe cambios claramente

---

## 📚 Recursos Útiles

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [H2 Database](https://www.h2database.com/)
- [MDN Web Docs (JavaScript/HTML/CSS)](https://developer.mozilla.org/)
- [RESTful API Best Practices](https://restfulapi.net/)

---

## 📄 Licencia

Proyecto de código abierto para equipo Ronyc.

---

**Última actualización**: 03 de Junio de 2024
**Versión**: 0.0.1-SNAPSHOT
**Estado**: En desarrollo
