# 📋 Estándares de Notificaciones del Sistema

## 🎯 **Tipos de Entidades (relatedEntityType)**

### **📊 Entidades Principales:**
- **`BUDGET`** - Presupuestos y solicitudes
- **`USER`** - Usuarios y registros
- **`PROJECT`** - Proyectos
- **`TICKET`** - Tickets de soporte
- **`CLIENT`** - Clientes
- **`INVOICE`** - Facturas

### **📝 Tipos de Notificaciones (type)**

#### **💰 Presupuestos:**
- **`BUDGET_REQUEST`** - Nueva solicitud de presupuesto
- **`BUDGET_PENDING`** - Presupuesto pendiente de aprobación
- **`BUDGET_APPROVED`** - Presupuesto aprobado
- **`BUDGET_REJECTED`** - Presupuesto rechazado
- **`BUDGET_IN_REVIEW`** - Presupuesto en revisión
- **`BUDGET_UPDATED`** - Presupuesto actualizado

#### **👤 Usuarios:**
- **`USER_REGISTRATION`** - Nuevo registro de usuario
- **`FIRST_LOGIN`** - Primer acceso del usuario
- **`USER_UPDATE`** - Actualización de usuario

#### **📊 Estadísticas:**
- **`DAILY_STATS`** - Estadísticas diarias
- **`WEEKLY_STATS`** - Estadísticas semanales
- **`MONTHLY_STATS`** - Estadísticas mensuales

#### **🚨 Sistema:**
- **`SYSTEM_ERROR`** - Error crítico del sistema
- **`UNUSUAL_ACTIVITY`** - Actividad inusual detectada

#### **🎫 Tickets:**
- **`TICKET_NEW`** - Nuevo ticket
- **`TICKET_RESOLVED`** - Ticket resuelto
- **`TICKET_CLOSED`** - Ticket cerrado
- **`TICKET_UPDATED`** - Ticket actualizado

#### **📁 Proyectos:**
- **`PROJECT_CREATED`** - Proyecto creado
- **`PROJECT_UPDATED`** - Proyecto actualizado
- **`PROJECT_COMPLETED`** - Proyecto completado

## 🎨 **Prioridades (priority)**

- **`high`** - Crítico, requiere atención inmediata
- **`medium`** - Importante, revisar pronto
- **`low`** - Informativo, revisar cuando sea posible

## 👥 **Roles Destino (targetRole)**

- **`admin`** - Administradores del sistema
- **`client`** - Clientes
- **`user`** - Usuarios generales

## 📋 **Reglas de Implementación**

### **✅ Debe hacer:**
1. **Usar MAYÚSCULAS** para `relatedEntityType`
2. **Usar MAYÚSCULAS** para `type`
3. **Usar minúsculas** para `priority`
4. **Usar minúsculas** para `targetRole`
5. **Validar campos** antes de crear notificaciones
6. **Manejar errores** sin fallar la operación principal

### **❌ No hacer:**
1. **Mezclar mayúsculas/minúsculas** en tipos de entidades
2. **Usar strings literales** sin constantes
3. **Ignorar errores** de notificaciones
4. **Crear notificaciones** sin validar datos

## 🔧 **Ejemplo de Implementación Correcta:**

```java
// ✅ CORRECTO
Notification notification = new Notification(
    "BUDGET_REQUEST",           // type en MAYÚSCULAS
    "Nuevo Presupuesto",        // title
    "Descripción del presupuesto", // message
    "high"                      // priority en minúsculas
);
notification.setTargetRole("admin");           // targetRole en minúsculas
notification.setRelatedEntityType("BUDGET");   // relatedEntityType en MAYÚSCULAS
notification.setRelatedEntityId(budgetId);

// ❌ INCORRECTO
Notification notification = new Notification(
    "budget_request",           // type en minúsculas
    "Nuevo Presupuesto",
    "Descripción del presupuesto",
    "HIGH"                      // priority en MAYÚSCULAS
);
notification.setTargetRole("ADMIN");           // targetRole en MAYÚSCULAS
notification.setRelatedEntityType("budget");   // relatedEntityType en minúsculas
```

## 📊 **Verificación de Consistencia**

Para verificar que no hay inconsistencias en la base de datos:

```sql
-- Verificar tipos de entidades
SELECT DISTINCT related_entity_type FROM notifications;

-- Verificar tipos de notificaciones
SELECT DISTINCT type FROM notifications;

-- Verificar prioridades
SELECT DISTINCT priority FROM notifications;

-- Verificar roles destino
SELECT DISTINCT target_role FROM notifications;
```

## 🚀 **Mantenimiento**

- **Revisar mensualmente** la consistencia de datos
- **Actualizar este documento** cuando se añadan nuevos tipos
- **Validar en pruebas** que se usen los estándares correctos
- **Documentar cambios** en el historial de versiones 