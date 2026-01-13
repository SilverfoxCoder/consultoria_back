# Consultoría Backend

Sistema completo de gestión de consultoría con notificaciones en tiempo real desarrollado con Spring Boot.

## 🚀 Inicio Rápido

### Prerrequisitos
- **Java 17+** - Versión de Java requerida
- **Maven 3.6+** - Gestor de dependencias
- **MySQL 8.0+** - Base de datos

### Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd consultoria-back
```

2. **Configurar base de datos**
```bash
# Crear base de datos si no existe
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS codethics;"

# Ejecutar script de configuración
mysql -u root -p -e "source database_setup.sql"

# O ejecutar migración manual
mysql -u root -p codethics < src/main/resources/db/migration/V1__init.sql
```

3. **Compilar y ejecutar**
```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
```

4. **Verificar funcionamiento**
```bash
# Probar endpoint de proyectos
curl http://localhost:8080/api/projects

# Acceder a Swagger UI
# http://localhost:8080/swagger-ui.html
```

## 📚 Documentación

- **[Documentación Completa](PROJECT_DOCUMENTATION.md)** - Guía detallada del proyecto
- **[Script de Base de Datos](database_setup.sql)** - Configuración de BD consolidada
- **[Documentación de Migraciones](MIGRATION_DOCUMENTATION.md)** - Guía de migraciones Flyway

## 🔧 Tecnologías

- **Backend**: Spring Boot 3.2.5
- **Base de Datos**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **Migraciones**: Flyway (configurado pero temporalmente deshabilitado)
- **Documentación API**: Swagger/OpenAPI
- **Seguridad**: Spring Security
- **WebSocket**: STOMP para notificaciones en tiempo real
- **Notificaciones**: Sistema completo integrado con presupuestos

## 📁 Estructura del Proyecto

```
src/main/java/com/xperiecia/consultoria/
├── api/           # Controllers REST
├── domain/        # Entities y Repositories
├── dto/           # Data Transfer Objects
└── infrastructure/ # Configuraciones

src/main/resources/
├── db/migration/  # Migraciones Flyway
│   └── V1__init.sql
└── application.yml # Configuración principal
```

## 🔌 Endpoints Principales

- `GET /api/projects` - Gestión de proyectos
- `GET /api/clients` - Gestión de clientes
- `GET /api/users` - Gestión de usuarios
- `GET /api/project-teams` - Equipos de proyecto
- `GET /api/invoices` - Gestión de facturas
- `GET /api/budgets` - Gestión de presupuestos
- `GET /api/notifications` - Sistema de notificaciones

## 🌐 Configuración

- **Puerto**: 8080
- **Base de datos**: codethics
- **CORS**: Configurado para localhost:3000
- **Migraciones**: Flyway temporalmente deshabilitado

## 🗄️ Base de Datos

### Tablas Principales
- **users** - Usuarios del sistema
- **clients** - Clientes de la consultoría
- **projects** - Proyectos de consultoría
- **project_team** - Equipos de proyecto
- **tasks** - Tareas de proyecto
- **invoices** - Facturas
- **budgets** - Presupuestos
- **notifications** - Sistema de notificaciones
- **analytics** - Datos analíticos

### Datos de Prueba
- **Usuario admin**: admin@codexcore.com / admin123
- **Usuario cliente**: cliente@empresa.com / admin123  
- **Cliente de prueba**: cliente.prueba@example.com / admin123

## 📝 Estado del Proyecto

✅ **Completado**
- Configuración de base de datos
- Endpoints CRUD completos  
- Configuración CORS
- DTOs implementados
- Documentación consolidada
- Migraciones limpias y documentadas
- Sistema de notificaciones completo
- Integración automática con presupuestos
- WebSocket configurado para tiempo real
- Gestión completa de presupuestos

🔄 **En Desarrollo**
- Funcionalidades adicionales según requerimientos

---

*Desarrollado con ❤️ usando Spring Boot* 
