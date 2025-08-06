# Documentación Consolidada del Proyecto Consultoría Backend

## 📋 Índice
1. [Configuración de Base de Datos](#configuración-de-base-de-datos)
2. [API Endpoints](#api-endpoints)
3. [Configuración CORS](#configuración-cors)
4. [Solución WebSocket](#solución-websocket)
5. [Integración Frontend](#integración-frontend)
6. [Implementación CRUD](#implementación-crud)
7. [Endpoints de Estado del Sistema](#endpoints-de-estado-del-sistema)
8. [Sistema de Notificaciones](#sistema-de-notificaciones)

---

## 🗄️ Configuración de Base de Datos

### Script de Configuración
El archivo `database_setup.sql` contiene todas las operaciones necesarias para:
- Verificar la estructura de la base de datos
- Arreglar problemas de foreign keys
- Limpiar datos duplicados
- Verificar la integridad de los datos

### Ejecución del Script
```bash
mysql -u root -p -e "source database_setup.sql"
```

### Tablas Principales
- `projects` - Gestión de proyectos
- `clients` - Gestión de clientes
- `users` - Gestión de usuarios
- `project_team` - Equipos de proyecto
- `invoices` - Facturas
- `invoice_items` - Items de factura
- `analytics` - Datos analíticos

---

## �� API Endpoints

### Gestión de Proyectos
- `GET /api/projects` - Obtener todos los proyectos
- `GET /api/projects/{id}` - Obtener proyecto por ID
- `GET /api/projects/client/{clientId}` - Obtener proyectos por cliente
- `GET /api/projects/status/{status}` - Obtener proyectos por estado
- `GET /api/projects/priority/{priority}` - Obtener proyectos por prioridad
- `GET /api/projects/active` - Obtener proyectos activos
- `GET /api/projects/low-progress` - Obtener proyectos con progreso bajo
- `GET /api/projects/over-budget` - Obtener proyectos que exceden presupuesto
- `POST /api/projects` - Crear nuevo proyecto
- `PUT /api/projects/{id}` - Actualizar proyecto
- `DELETE /api/projects/{id}` - Eliminar proyecto (elimina automáticamente dependencias)
- `GET /api/projects/stats` - Estadísticas de proyectos

### Equipos de Proyecto
- `GET /api/project-teams` - Obtener todos los equipos
- `POST /api/project-teams` - Crear nuevo miembro
- `PUT /api/project-teams/{id}` - Actualizar miembro
- `DELETE /api/project-teams/{id}` - Eliminar miembro

### Clientes
- `GET /api/clients` - Obtener todos los clientes
- `GET /api/clients/{id}` - Obtener cliente por ID
- `GET /api/clients/status/{status}` - Obtener clientes por estado
- `POST /api/clients` - Crear nuevo cliente
- `PUT /api/clients/{id}` - Actualizar cliente
- `DELETE /api/clients/{id}` - Eliminar cliente (respuesta JSON)

### Usuarios
- `GET /api/users` - Obtener todos los usuarios
- `GET /api/users/{id}` - Obtener usuario por ID
- `POST /api/users` - Crear nuevo usuario
- `PUT /api/users/{id}` - Actualizar usuario
- `DELETE /api/users/{id}` - Eliminar usuario (respuesta JSON)

### Facturas
- `GET /api/invoices` - Obtener todas las facturas
- `POST /api/invoices` - Crear nueva factura
- `PUT /api/invoices/{id}` - Actualizar factura
- `DELETE /api/invoices/{id}` - Eliminar factura (respuesta JSON)

### Tareas
- `GET /api/tasks` - Obtener todas las tareas
- `GET /api/tasks/{id}` - Obtener tarea por ID
- `POST /api/tasks` - Crear nueva tarea
- `PUT /api/tasks/{id}` - Actualizar tarea
- `DELETE /api/tasks/{id}` - Eliminar tarea (respuesta JSON)

### Presupuestos
- `POST /api/budgets` - Crear un nuevo presupuesto
- `POST /api/budgets/client/{clientId}` - Crear presupuesto para un cliente específico
- `GET /api/budgets` - Obtener todos los presupuestos
- `GET /api/budgets/{id}` - Obtener un presupuesto por ID
- `GET /api/budgets/client/{clientId}` - Obtener presupuestos de un cliente
- `GET /api/budgets/status/{status}` - Obtener presupuestos por estado
- `PUT /api/budgets/{id}/status` - Actualizar estado de un presupuesto
- `DELETE /api/budgets/{id}` - Eliminar un presupuesto (respuesta JSON)
- `GET /api/budgets/statistics` - Obtener estadísticas de presupuestos

### Dashboard
- `GET /api/dashboard/data` - Obtener datos completos del dashboard
- `GET /api/dashboard/summary` - Obtener resumen del dashboard

### Estado del Sistema
- `GET /api/status/system` - Estado general del sistema
- `GET /api/status/metrics` - Métricas detalladas
- `GET /api/status/health` - Salud del sistema
- `GET /api/health` - Salud del sistema (endpoint alternativo)
- `GET /api/status/connection` - Estado de conexión del sistema
- `GET /api/status/services` - Estado de los servicios
- `GET /api/status/database` - Estado específico de la base de datos

### Autenticación
- `POST /api/auth/login` - Autenticar usuario
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/verify` - Verificar autenticación
- `GET /api/auth/first-login/{userId}` - Verificar si es el primer login
- `POST /api/auth/change-password` - Cambiar contraseña

### Registro de Usuarios
- `POST /api/register/user` - Registrar nuevo usuario
- `GET /api/register/check-email/{email}` - Verificar disponibilidad de email

---

## 🌐 Configuración CORS

### Configuración Actual
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplicar a todos los endpoints
                .allowedOrigins("http://localhost:3000", "https://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "X-Requested-With")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### Orígenes Permitidos
- `http://localhost:3000` - Frontend React

---

## 🔄 Solución WebSocket

### Problema
El frontend intentaba conectarse a WebSocket en `ws://localhost:3000/ws` pero no había servidor WebSocket implementado.

### Solución Implementada
Se creó `WebSocketSimulatorController` con endpoints REST que simulan funcionalidad WebSocket:

- `GET /api/ws/status` - Estado de conexión
- `POST /api/ws/message` - Enviar mensaje

### Adaptación Frontend
El frontend debe cambiar de WebSocket a REST:
```javascript
// Antes (WebSocket)
const ws = new WebSocket('ws://localhost:3000/ws');

// Ahora (REST)
const response = await fetch('http://localhost:8080/api/ws/status');
```

---

## 🎨 Integración Frontend

### Configuración Base
- **Backend URL**: `http://localhost:8080`
- **Frontend URL**: `http://localhost:3000`
- **CORS**: Configurado para permitir comunicación

### DTOs Implementados
- `ProjectDTO` - Para proyectos
- `ClientDTO` - Para clientes
- `UserDTO` - Para usuarios
- `InvoiceDTO` - Para facturas
- `ProjectTeamDTO` - Para equipos de proyecto

### Ejemplo de Uso
```javascript
// Crear proyecto
const project = {
  name: "Nuevo Proyecto",
  clientId: 1,
  status: "PLANIFICACION",
  startDate: "2025-08-01",
  endDate: "2025-12-31",
  budget: 50000.00
};

const response = await fetch('http://localhost:8080/api/projects', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(project)
});
```

---

## 🔧 Implementación CRUD

### Patrón Implementado
1. **DTOs** - Para controlar serialización JSON
2. **Repositories** - Para acceso a datos
3. **Controllers** - Para endpoints REST
4. **Entities** - Para mapeo de base de datos

### Estructura de Archivos
```
src/main/java/com/codethics/consultoria/
├── api/           # Controllers
├── domain/        # Entities y Repositories
├── dto/           # Data Transfer Objects
└── infrastructure/ # Configuraciones
```

### Validaciones Implementadas
- Verificación de existencia de registros relacionados
- Validación de tipos de datos
- Manejo de errores con ResponseEntity

---

## 📊 Endpoints de Estado del Sistema

### Propósito
Los endpoints de estado del sistema proporcionan información en tiempo real sobre el estado general de la aplicación, métricas y estadísticas para el dashboard del frontend.

### Endpoints Disponibles

#### 1. **GET /api/status/system**
Obtiene el estado general del sistema.

**Respuesta:**
```json
{
  "status": "online",
  "timestamp": "2025-08-01T04:14:23.307618700",
  "version": "1.0.0",
  "environment": "development",
  "totalProjects": 3,
  "totalClients": 4,
  "totalUsers": 2,
  "totalInvoices": 2,
  "totalTasks": 0,
  "databaseStatus": "connected",
  "lastCheck": "2025-08-01T04:14:23.307618700"
}
```

#### 2. **GET /api/status/metrics**
Obtiene métricas detalladas del sistema.

**Respuesta:**
```json
{
  "projects": {
    "total": 3,
    "active": 1,
    "completed": 0,
    "planning": 2,
    "cancelled": 0,
    "paused": 0
  },
  "clients": {
    "total": 4,
    "active": 1,
    "prospect": 3,
    "inactive": 0
  },
  "users": {
    "total": 2,
    "active": 2,
    "inactive": 0
  },
  "invoices": {
    "total": 2,
    "draft": 2,
    "sent": 0,
    "paid": 0,
    "overdue": 0
  },
  "tasks": {
    "total": 0,
    "pending": 0,
    "inProgress": 0,
    "completed": 0,
    "cancelled": 0,
    "paused": 0
  },
  "timeEntries": {
    "totalEntries": 0,
    "totalHours": 0.0,
    "billableHours": 0.0,
    "pendingApproval": 0,
    "approved": 0,
    "rejected": 0
  },
  "timestamp": "2025-08-01T04:14:23.307618700",
  "systemUptime": "running"
}
```

#### 3. **GET /api/status/health**
Verifica la salud del sistema.

**Respuesta:**
```json
{
  "status": "healthy",
  "database": "connected",
  "timestamp": "2025-08-01T04:14:41.777985700",
  "checks": {
    "database": "ok",
    "api": "ok",
    "memory": "ok"
  }
}
```

#### 4. **GET /api/status/connection**
Obtiene el estado de conexión del sistema.

**Respuesta:**
```json
{
  "status": "connected",
  "timestamp": "2025-08-01T15:11:38.527632",
  "latency": "5ms",
  "uptime": "99.9%",
  "lastCheck": "2025-08-01T15:11:38.527632"
}
```

#### 5. **GET /api/status/services**
Obtiene el estado de los servicios del sistema.

**Respuesta:**
```json
{
  "database": "running",
  "api": "running",
  "authentication": "running",
  "fileStorage": "running",
  "email": "running",
  "timestamp": "2025-08-01T15:11:44.650285",
  "overallStatus": "healthy"
}
```

#### 6. **GET /api/status/database**
Obtiene el estado específico de la base de datos.

**Respuesta:**
```json
{
  "status": "connected",
  "type": "MySQL",
  "version": "8.0",
  "activeConnections": 5,
  "totalUsers": 3,
  "lastBackup": "2025-07-31T15:11:50.063544",
  "timestamp": "2025-08-01T15:11:50.063544"
}
```

### Implementación Técnica

#### Controlador: `SystemStatusController`
```java
@RestController
@RequestMapping("/api/status")
@Tag(name = "System Status", description = "API para estado del sistema y métricas")
public class SystemStatusController {
    // Métodos implementados:
    // - getSystemStatus()
    // - getSystemMetrics()
    // - getSystemHealth()
}
```

#### Características
- **Manejo de Errores**: Try-catch con respuestas de error apropiadas
- **Métricas en Tiempo Real**: Conteos actualizados de la base de datos
- **Información de Sistema**: Versión, entorno, timestamp
- **Estado de Base de Datos**: Verificación de conectividad

#### Uso en Frontend
```javascript
// Ejemplo de uso en React
const fetchSystemStatus = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/status/system');
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching system status:', error);
  }
};

const fetchMetrics = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/status/metrics');
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching metrics:', error);
  }
};
```

---

## 📊 Dashboard

### Propósito
Los endpoints del dashboard proporcionan datos específicos para el frontend, devolviendo información en el formato exacto que necesita la interfaz de usuario.

### Endpoints Disponibles

#### 1. **GET /api/dashboard/data**
Obtiene datos completos del dashboard con métricas detalladas.

**Respuesta:**
```json
{
  "metrics": {
    "projects": {
      "total": 1,
      "active": 1,
      "planning": 0,
      "completed": 0
    },
    "clients": {
      "total": 1,
      "active": 1,
      "prospect": 0
    },
    "users": {
      "total": 3,
      "active": 3
    },
    "invoices": {
      "total": 2,
      "draft": 2,
      "sent": 0,
      "paid": 0
    },
    "tasks": {
      "total": 0,
      "pending": 0,
      "inProgress": 0,
      "completed": 0
    }
  },
  "timestamp": "2025-08-01T17:14:46.123456",
  "status": "success"
}
```

#### 2. **GET /api/dashboard/summary**
Obtiene un resumen rápido de las métricas principales.

**Respuesta:**
```json
{
  "totalProjects": 1,
  "activeProjects": 1,
  "totalClients": 1,
  "activeClients": 1,
  "totalUsers": 3,
  "totalInvoices": 2,
  "totalTasks": 0,
  "timestamp": "2025-08-01T17:14:46.123456",
  "status": "success"
}
```

### Implementación Técnica

#### Controlador: `DashboardController`
```java
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "API para datos del dashboard")
public class DashboardController {
    // Métodos implementados:
    // - getDashboardData()
    // - getDashboardSummary()
}
```

#### Características
- **Datos en Tiempo Real**: Conteos actualizados de la base de datos
- **Formato Optimizado**: Respuestas estructuradas para el frontend
- **Manejo de Errores**: Try-catch con respuestas de error apropiadas
- **Métricas Detalladas**: Información completa por categoría

#### Uso en Frontend
```javascript
// Ejemplo de uso en React
const fetchDashboardData = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/dashboard/data');
    const data = await response.json();
    return data.metrics;
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
  }
};

const fetchDashboardSummary = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/dashboard/summary');
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching dashboard summary:', error);
  }
};
```

---

## 💰 Gestión de Presupuestos

### Propósito
El sistema de presupuestos permite a los clientes solicitar presupuestos para proyectos específicos, y a los administradores gestionar y responder a estas solicitudes.

### Endpoints Disponibles

#### 1. **POST /api/budgets**
Crear un nuevo presupuesto.

**Request Body:**
```json
{
  "title": "Desarrollo de app",
  "description": "App móvil para gestión de citas",
  "serviceType": "Desarrollo Móvil",
  "budget": 10000,
  "timeline": "2-3 meses",
  "additionalInfo": "Requiere integración con Google Calendar",
  "clientId": 4
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Desarrollo de app",
  "description": "App móvil para gestión de citas",
  "serviceType": "Desarrollo Móvil",
  "budget": 10000,
  "timeline": "2-3 meses",
  "additionalInfo": "Requiere integración con Google Calendar",
  "clientId": 4,
  "clientName": "Jose",
  "status": "PENDIENTE",
  "statusDisplay": "Pendiente",
  "createdAt": "2025-08-01T17:30:00.000000",
  "updatedAt": "2025-08-01T17:30:00.000000",
  "responseDate": null,
  "responseNotes": null,
  "approvedBudget": null,
  "approvedTimeline": null
}
```

#### 2. **POST /api/budgets/client/{clientId}**
Crear presupuesto para un cliente específico (el clientId se toma de la URL).

#### 3. **GET /api/budgets**
Obtener todos los presupuestos ordenados por fecha de creación.

#### 4. **GET /api/budgets/{id}**
Obtener un presupuesto específico por ID.

#### 5. **GET /api/budgets/client/{clientId}**
Obtener todos los presupuestos de un cliente específico.

#### 6. **GET /api/budgets/status/{status}**
Obtener presupuestos por estado (PENDIENTE, EN_REVISION, APROBADO, RECHAZADO, CANCELADO).

#### 7. **PUT /api/budgets/{id}/status**
Actualizar el estado de un presupuesto.

**Request Body:**
```json
{
  "status": "APROBADO",
  "responseNotes": "Presupuesto aprobado con modificaciones",
  "approvedBudget": 12000,
  "approvedTimeline": "3-4 meses"
}
```

#### 8. **DELETE /api/budgets/{id}**
Eliminar un presupuesto.

**Response:**
```json
{
  "message": "Presupuesto eliminado correctamente",
  "id": 1,
  "success": true
}
```

#### 9. **GET /api/budgets/statistics**
Obtener estadísticas de presupuestos.

**Response:**
```json
{
  "total": 5,
  "pending": 2,
  "inReview": 1,
  "approved": 1,
  "rejected": 1
}
```

### Implementación Técnica

#### Entidad: `Budget`
```java
@Entity
@Table(name = "budgets")
public class Budget {
    // Campos principales:
    // - title: Título del proyecto
    // - description: Descripción detallada
    // - serviceType: Tipo de servicio
    // - budget: Presupuesto estimado
    // - timeline: Timeline estimado
    // - additionalInfo: Información adicional
    // - client: Cliente asociado
    // - status: Estado del presupuesto
    // - responseDate: Fecha de respuesta
    // - responseNotes: Notas de respuesta
    // - approvedBudget: Presupuesto aprobado
    // - approvedTimeline: Timeline aprobado
}
```

#### Estados del Presupuesto
- **PENDIENTE**: Presupuesto recién creado, esperando revisión
- **EN_REVISION**: Presupuesto siendo revisado por el administrador
- **APROBADO**: Presupuesto aprobado con o sin modificaciones
- **RECHAZADO**: Presupuesto rechazado
- **CANCELADO**: Presupuesto cancelado

#### Controlador: `BudgetController`
```java
@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "API para gestión de presupuestos")
public class BudgetController {
    // Métodos implementados:
    // - createBudget()
    // - createBudgetForClient()
    // - getAllBudgets()
    // - getBudgetById()
    // - getBudgetsByClient()
    // - getBudgetsByStatus()
    // - updateBudgetStatus()
    // - deleteBudget()
    // - getBudgetStatistics()
}
```

#### Características
- **Validación de Cliente**: Verifica que el cliente existe antes de crear el presupuesto
- **Estados Automáticos**: Maneja automáticamente las fechas de respuesta
- **Respuestas Estructuradas**: DTOs específicos para request y response
- **Estadísticas**: Endpoint para obtener métricas de presupuestos
- **Ordenamiento**: Presupuestos ordenados por fecha de creación (más recientes primero)

#### Uso en Frontend
```javascript
// Ejemplo de creación de presupuesto
const createBudget = async (budgetData) => {
  try {
    const response = await fetch('http://localhost:8080/api/budgets', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(budgetData)
    });
    
    if (response.status === 201) {
      const budget = await response.json();
      console.log('Presupuesto creado:', budget);
    }
  } catch (error) {
    console.error('Error creando presupuesto:', error);
  }
};

// Ejemplo de obtención de presupuestos por cliente
const getClientBudgets = async (clientId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/budgets/client/${clientId}`);
    const budgets = await response.json();
    return budgets;
  } catch (error) {
    console.error('Error obteniendo presupuestos:', error);
  }
};
```

---

## 🔧 Manejo de Dependencias

### Eliminación de Proyectos
Al eliminar un proyecto, el sistema automáticamente elimina las siguientes dependencias:
- **Miembros del equipo** (`project_team`) - Eliminación en cascada
- **Tareas** (`tasks`) - Eliminación en cascada  
- **Entradas de tiempo** (`time_entries`) - Eliminación en cascada

### Restricciones de Clave Foránea
- Las tablas `project_team` y `tasks` tienen `ON DELETE CASCADE`
- La tabla `time_entries` tiene `ON DELETE SET NULL` pero se elimina manualmente
- Si hay errores de restricción, se proporciona un mensaje específico

---

## 🚀 Comandos Útiles

### Compilar Proyecto
```bash
mvn clean compile
```

### Ejecutar Aplicación
```bash
mvn spring-boot:run
```

### Verificar Base de Datos
```bash
mysql -u root -p -e "source database_setup.sql"
```

### Probar Endpoints
```bash
# Proyectos
curl -X GET http://localhost:8080/api/projects

# Equipos
curl -X GET http://localhost:8080/api/project-teams

# Clientes
curl -X GET http://localhost:8080/api/clients

# Estado del sistema
curl -X GET http://localhost:8080/api/status/system
curl -X GET http://localhost:8080/api/status/metrics
curl -X GET http://localhost:8080/api/status/health
```

---

## 📝 Notas Importantes

### Problemas Resueltos
1. ✅ Foreign key constraints incompatibles
2. ✅ Datos duplicados en project_team
3. ✅ Configuración CORS
4. ✅ Serialización JSON circular
5. ✅ WebSocket no implementado
6. ✅ Endpoints de estado del sistema faltantes

### Estado Actual
- ✅ Base de datos configurada correctamente
- ✅ Todos los endpoints funcionando
- ✅ CORS configurado
- ✅ DTOs implementados
- ✅ Documentación consolidada
- ✅ Endpoints de estado del sistema implementados

---

*Última actualización: Agosto 2025* 

---

## 🔧 Mejoras en Endpoints DELETE

### Problema Resuelto
Los endpoints DELETE devolvían `ResponseEntity<Void>` (respuesta vacía), causando errores en el frontend:
```
Error: Failed to execute 'json' on 'Response': Unexpected end of JSON input
```

### Solución Implementada
Todos los endpoints DELETE ahora devuelven respuestas JSON consistentes:

**Respuesta Exitosa:**
```json
{
  "success": true,
  "id": 123,
  "message": "Elemento eliminado correctamente"
}
```

**Respuesta de Error:**
```json
{
  "success": false,
  "id": 123,
  "message": "Error al eliminar el elemento",
  "error": "Detalles del error"
}
```

### Endpoints Mejorados
- ✅ `DELETE /api/clients/{id}` - Eliminar cliente
- ✅ `DELETE /api/users/{id}` - Eliminar usuario  
- ✅ `DELETE /api/invoices/{id}` - Eliminar factura
- ✅ `DELETE /api/tasks/{id}` - Eliminar tarea
- ✅ `DELETE /api/projects/{id}` - Eliminar proyecto

--- 

---

## 🔐 Autenticación

### Endpoints Disponibles

#### 1. **POST /api/auth/login**
Autentica un usuario con email y contraseña.

**Request:**
```json
{
  "email": "admin@codexcore.com",
  "password": "password"
}
```

**Response (Éxito):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "token": "token_1_1754016596430",
  "user": {
    "id": 1,
    "name": "Administrador",
    "email": "admin@codexcore.com",
    "role": "admin"
  }
}
```

**Response (Error):**
```json
{
  "success": false,
  "message": "Credenciales incorrectas"
}
```

#### 2. **POST /api/auth/logout**
Cierra la sesión del usuario.

**Response:**
```json
{
  "success": true,
  "message": "Sesión cerrada correctamente"
}
```

#### 3. **GET /api/auth/verify**
Verifica si el usuario está autenticado.

**Response:**
```json
{
  "authenticated": true,
  "message": "Usuario autenticado"
}
```

### Credenciales de Prueba
- **Email**: `admin@codexcore.com`
- **Contraseña**: `password`

### Características
- ✅ Validación de credenciales
- ✅ Registro de historial de login
- ✅ Generación de tokens de sesión
- ✅ Manejo de errores apropiado
- ✅ Respuestas JSON consistentes

--- 

---

## 🔐 Sistema de Cambio de Contraseña y Registro

### Cambio de Contraseña para Primer Login

#### 1. **GET /api/auth/first-login/{userId}**
Verifica si es el primer login del usuario.

**Response:**
```json
{
  "userId": 1,
  "isFirstLogin": false,
  "loginCount": 2,
  "lastLogin": "2025-08-01T04:49:34"
}
```

#### 2. **POST /api/auth/change-password**
Cambia la contraseña del usuario.

**Request:**
```json
{
  "userId": 1,
  "currentPassword": "password",
  "newPassword": "nueva123",
  "confirmPassword": "nueva123"
}
```

**Response (Éxito):**
```json
{
  "success": true,
  "message": "Contraseña cambiada exitosamente",
  "userId": 1
}
```

**Response (Error):**
```json
{
  "success": false,
  "message": "Contraseña actual incorrecta"
}
```

### Registro de Usuarios

#### 1. **POST /api/register/user**
Registra un nuevo usuario desde el área de cliente o llamadas a la acción.

**Request:**
```json
{
  "name": "Nuevo Usuario",
  "email": "nuevo@test.com",
  "password": "password123",
  "confirmPassword": "password123",
  "phone": "123456789",
  "role": "user"
}
```

**Response (Éxito):**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "user": {
    "id": 4,
    "name": "Nuevo Usuario",
    "email": "nuevo@test.com",
    "role": "user",
    "phone": "123456789"
  }
}
```

#### 2. **GET /api/register/check-email/{email}**
Verifica si un email está disponible para registro.

**Response:**
```json
{
  "email": "nuevo@test.com",
  "available": false,
  "message": "Email ya registrado"
}
```

### Flujo de Implementación

#### Para el Área de Cliente:
1. **Login exitoso** → Verificar si es primer login
2. **Si es primer login** → Mostrar formulario de cambio de contraseña
3. **Cambio de contraseña** → Redirigir al dashboard

#### Para Llamadas a la Acción del Home:
1. **Botón "Registrarse"** → Abrir formulario de registro
2. **Validación en tiempo real** → Verificar disponibilidad de email
3. **Registro exitoso** → Redirigir al login

### Validaciones Implementadas
- ✅ Verificación de contraseña actual
- ✅ Confirmación de nueva contraseña
- ✅ Validación de email único
- ✅ Verificación de campos obligatorios
- ✅ Validación de formato de email
- ✅ Verificación de disponibilidad de email en tiempo real

--- 

---

## 📁 Estructura de Archivos Frontend

### Servicios API
- `api.js` - Configuración base de la API
- `authService.js` - Autenticación y gestión de usuarios
- `clientService.js` - Gestión de clientes
- `projectService.js` - **ACTUALIZADO: Gestión de proyectos (incluye getProjectsByClient)**
- `taskService.js` - Gestión de tareas
- `invoiceService.js` - Gestión de facturas
- `userService.js` - Gestión de usuarios
- `statusService.js` - Estado del sistema
- `dashboardService.js` - Datos del dashboard
- `budgetService.js` - **ACTUALIZADO: Gestión de presupuestos (clase BudgetService)**
- `ticketService.js` - **NUEVO: Gestión de tickets de soporte**

### Componentes Principales
- `App.js`

### Servicios del Frontend

#### Servicio de Presupuestos (`budgetService.js`)
El archivo `budgetService.js` proporciona todas las funciones necesarias para interactuar con los endpoints de presupuestos del backend.

**Funciones Disponibles:**
1. **`createBudget(budgetData)`** - Crear un nuevo presupuesto
2. **`getAllBudgets()`** - Obtener todos los presupuestos
3. **`getBudgetsByClient(clientId)`** - Obtener presupuestos por cliente
4. **`getBudgetById(budgetId)`** - Obtener un presupuesto específico
5. **`getBudgetsByStatus(status)`** - Obtener presupuestos por estado
6. **`updateBudgetStatus(budgetId, statusData)`** - Actualizar estado de presupuesto
7. **`deleteBudget(budgetId)`** - Eliminar un presupuesto
8. **`getBudgetStatistics()`** - Obtener estadísticas de presupuestos
9. **`createBudgetForClient(clientId, budgetData)`** - Crear presupuesto para cliente específico

#### Servicio de Tickets (`ticketService.js`)
El archivo `ticketService.js` maneja todas las operaciones relacionadas con tickets de soporte técnico.

**Funciones Disponibles:**
1. **`getTicketsByClient(clientId)`** - Obtener tickets por cliente
2. **`createTicket(ticketData)`** - Crear un nuevo ticket
3. **`getAllTickets()`** - Obtener todos los tickets
4. **`getTicketById(ticketId)`** - Obtener un ticket específico
5. **`updateTicketStatus(ticketId, statusData)`** - Actualizar estado de ticket
6. **`deleteTicket(ticketId)`** - Eliminar un ticket
7. **`getTicketStatistics()`** - Obtener estadísticas de tickets

#### Servicio de Proyectos (`projectService.js`)
El archivo `projectService.js` maneja todas las operaciones relacionadas con proyectos.

**Funciones Disponibles:**
1. **`getProjectsByClient(clientId)`** - Obtener proyectos por cliente
2. **`createProject(projectData)`** - Crear un nuevo proyecto
3. **`getAllProjects()`** - Obtener todos los proyectos
4. **`getProjectById(projectId)`** - Obtener un proyecto específico
5. **`updateProjectStatus(projectId, statusData)`** - Actualizar estado de proyecto
6. **`deleteProject(projectId)`** - Eliminar un proyecto
7. **`getProjectStatistics()`** - Obtener estadísticas de proyectos
8. **`getProjectsByStatus(status)`** - Obtener proyectos por estado

#### Ejemplo de Uso:

```javascript
import { getBudgetsByClient, createBudget } from './budgetService';
import { getTicketsByClient } from './ticketService';
import { getProjectsByClient } from './projectService';

// Obtener presupuestos de un cliente
const budgets = await getBudgetsByClient(4);

// Obtener tickets de un cliente
const tickets = await getTicketsByClient(4);

// Obtener proyectos de un cliente
const projects = await getProjectsByClient(4);

// Crear un nuevo presupuesto
const newBudget = await createBudget({
    title: "Desarrollo de aplicación web",
    description: "Sistema de gestión de inventarios",
    serviceType: "Desarrollo Web",
    budget: 15000,
    timeline: "3-4 meses",
    additionalInfo: "Requiere integración con Stripe",
    clientId: 4
});
```

#### Integración con Componentes:

- **ClientDashboard.jsx**: Usa `getBudgetsByClient()` para mostrar presupuestos del cliente
- **ClientBudgets.jsx**: Usa todas las funciones para gestión completa de presupuestos
- **ClientSupport.jsx**: Usa `getTicketsByClient()` para mostrar tickets del cliente
- **ClientAnalytics.jsx**: Usa `getProjectsByClient()` para mostrar proyectos del cliente
- **Dashboard.jsx**: Usa `getBudgetStatistics()` para mostrar estadísticas

---

## 🔔 Sistema de Notificaciones

### Descripción
Sistema completo de notificaciones en tiempo real que se integra automáticamente con presupuestos, tickets y proyectos.

### Componentes Implementados

#### Backend
- **Entidad**: `Notification.java` - Modelo de datos para notificaciones
- **Repository**: `NotificationRepository.java` - Consultas JPA personalizadas
- **Service**: `NotificationService.java` - Lógica de negocio y WebSocket
- **Controller**: `NotificationController.java` - API REST endpoints
- **WebSocket**: `WebSocketConfig.java` - Configuración para tiempo real

#### Base de Datos
```sql
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    target_user_id BIGINT,
    target_role VARCHAR(50),
    related_entity_id BIGINT,
    related_entity_type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSON
);
```

### API Endpoints de Notificaciones

#### Obtener Notificaciones
```
GET /api/notifications/user/{userId}?userRole={role}&page=0&size=20
```
**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "type": "BUDGET_PENDING",
      "title": "Nuevo Presupuesto Pendiente",
      "message": "Nuevo presupuesto requiere aprobación",
      "priority": "high",
      "targetUserId": null,
      "targetRole": "admin",
      "relatedEntityId": 11,
      "relatedEntityType": "budget",
      "read": false,
      "createdAt": "2025-01-02T15:30:00"
    }
  ],
  "totalElements": 3,
  "totalPages": 1
}
```

#### Estadísticas de Notificaciones
```
GET /api/notifications/user/{userId}/stats?userRole={role}
```
**Respuesta:**
```json
{
  "total": 5,
  "unread": 2
}
```

#### Marcar como Leída
```
PUT /api/notifications/{id}/read
```
**Respuesta:**
```json
{
  "success": true,
  "message": "Notificación marcada como leída",
  "notificationId": 1
}
```

#### Marcar Todas como Leídas
```
PUT /api/notifications/user/{userId}/read-all?userRole={role}
```
**Respuesta:**
```json
{
  "success": true,
  "message": "Todas las notificaciones han sido marcadas como leídas",
  "userId": 1
}
```

#### Eliminar Notificación
```
DELETE /api/notifications/{id}
```
**Respuesta:**
```json
{
  "success": true,
  "message": "Notificación eliminada correctamente",
  "notificationId": 1
}
```

#### Crear Notificación
```
POST /api/notifications
```
**Body:**
```json
{
  "type": "SYSTEM_ANNOUNCEMENT",
  "title": "Mantenimiento Programado",
  "message": "El sistema estará en mantenimiento mañana de 2-4 AM",
  "priority": "medium",
  "targetRole": "admin"
}
```

### Integración Automática

#### Con Presupuestos
El `BudgetController` genera automáticamente notificaciones:

**Al Crear Presupuesto:**
- Notificación para administradores: `BUDGET_PENDING`
- Título: "Nuevo Presupuesto Pendiente"
- Prioridad: `high`

**Al Actualizar Estado:**
- `APROBADO` → Notificación para cliente: `BUDGET_APPROVED`
- `RECHAZADO` → Notificación para cliente: `BUDGET_REJECTED`
- Prioridad: `medium`

#### Configuración WebSocket
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/notifications")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }
}
```

### Tipos de Notificaciones

#### Presupuestos
- `BUDGET_PENDING` - Nuevo presupuesto pendiente de revisión
- `BUDGET_APPROVED` - Presupuesto aprobado
- `BUDGET_REJECTED` - Presupuesto rechazado

#### Sistema
- `SYSTEM_ANNOUNCEMENT` - Anuncios del sistema
- `WELCOME` - Mensaje de bienvenida

#### Futuros (Preparados)
- `TICKET_NEW` - Nuevo ticket de soporte
- `PROJECT_MILESTONE` - Hito de proyecto completado
- `PROJECT_COMPLETED` - Proyecto completado
- `PROJECT_UPDATE` - Actualización de proyecto

### Prioridades
- `high` - Alta prioridad (presupuestos pendientes, tickets críticos)
- `medium` - Prioridad media (actualizaciones de estado)
- `low` - Prioridad baja (anuncios generales)

### Usuarios de Prueba
Para probar el sistema de notificaciones:

**Cliente de Prueba:**
- Email: `cliente.prueba@example.com`
- Password: `admin123`
- ID: 5

**Administrador:**
- Email: `admin@codexcore.com`
- Password: `admin123`
- ID: 1

### Estado del Sistema
✅ **Backend Completo** - Todos los endpoints funcionando
✅ **Integración con Presupuestos** - Notificaciones automáticas
✅ **WebSocket Configurado** - Listo para tiempo real
✅ **Base de Datos** - Tabla de notificaciones creada
✅ **API REST** - Todos los endpoints devuelven JSON válido
✅ **CORS Configurado** - Compatible con frontend React
