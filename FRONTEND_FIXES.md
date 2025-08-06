# 🔧 Correcciones Necesarias para el Frontend

## 🚨 **Problemas Identificados:**

### **1. URLs Incorrectas en adminService.js**
- **❌ Actual**: `/api/api/admin/notifications`
- **✅ Correcto**: `/api/admin/notifications`

### **2. Endpoint de Eliminación Faltante**
- **❌ Error**: `DELETE /api/admin/notifications/39` → 500
- **✅ Solucionado**: Endpoint añadido al backend

### **3. WebSocket Fallando**
- **❌ Error**: `WebSocket connection to 'ws://localhost:8080/ws/notifications/1' failed`
- **🔧 Problema**: Configuración de WebSocket

---

## 📋 **Correcciones Específicas:**

### **🔧 1. Corregir adminService.js**

```javascript
// ❌ INCORRECTO
const BASE_URL = 'http://localhost:8080/api';
const endpoint = `${BASE_URL}/api/admin/notifications`;

// ✅ CORRECTO
const BASE_URL = 'http://localhost:8080';
const endpoint = `${BASE_URL}/api/admin/notifications`;
```

**Archivos a revisar:**
- `src/services/adminService.js`
- `src/services/notificationService.js`

### **🔧 2. Endpoints Disponibles en Backend:**

#### **📊 Notificaciones de Administrador:**
- **`GET /api/admin/notifications`** - Obtener notificaciones con paginación
- **`DELETE /api/admin/notifications/{id}`** - Eliminar notificación específica
- **`PUT /api/admin/notifications/{id}/read`** - Marcar como leída

#### **📈 Estadísticas:**
- **`GET /api/admin/stats/summary`** - Resumen de estadísticas
- **`POST /api/admin/notifications/daily-stats`** - Enviar estadísticas diarias
- **`POST /api/admin/notifications/weekly-stats`** - Enviar estadísticas semanales
- **`POST /api/admin/notifications/monthly-stats`** - Enviar estadísticas mensuales

#### **🧪 Testing:**
- **`GET /api/admin/test`** - Test del controlador
- **`POST /api/admin/notifications/test/*`** - Simular diferentes tipos de notificaciones

### **🔧 3. Corregir notificationService.js**

```javascript
// ❌ INCORRECTO
const response = await fetch(`${API_BASE_URL}/notifications`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(notificationData)
});

// ✅ CORRECTO
const response = await fetch(`${API_BASE_URL}/notifications/create`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(notificationData)
});
```

### **🔧 4. Configuración de WebSocket**

**Backend WebSocket Endpoints:**
- **`/ws/notifications`** - Endpoint principal
- **`/topic/notifications/admin`** - Para administradores
- **`/topic/notifications/client`** - Para clientes
- **`/topic/notifications/user`** - Para usuarios generales

**Configuración Frontend:**
```javascript
// ✅ CONFIGURACIÓN CORRECTA
const ws = new WebSocket('ws://localhost:8080/ws/notifications');
ws.onopen = () => {
    // Suscribirse al topic correcto
    ws.send(JSON.stringify({
        destination: '/topic/notifications/admin',
        type: 'SUBSCRIBE'
    }));
};
```

---

## 🎯 **Pasos para Corregir:**

### **1. Verificar URLs Base**
```bash
# Buscar archivos con URLs incorrectas
grep -r "/api/api/" src/
grep -r "localhost:8080/api" src/
```

### **2. Corregir adminService.js**
```javascript
// Cambiar todas las URLs de:
const url = `${baseUrl}/api/admin/...`
// A:
const url = `${baseUrl}/admin/...`
```

### **3. Corregir notificationService.js**
```javascript
// Cambiar endpoint de creación de:
const url = `${baseUrl}/notifications`
// A:
const url = `${baseUrl}/notifications/create`
```

### **4. Verificar WebSocket**
```javascript
// Asegurar que la URL del WebSocket es correcta:
const wsUrl = 'ws://localhost:8080/ws/notifications';
```

---

## ✅ **Verificación de Correcciones:**

### **🧪 Probar Endpoints:**
```bash
# 1. Obtener notificaciones
curl http://localhost:8080/api/admin/notifications

# 2. Eliminar notificación
curl -X DELETE http://localhost:8080/api/admin/notifications/35

# 3. Marcar como leída
curl -X PUT http://localhost:8080/api/admin/notifications/36/read

# 4. Estadísticas
curl http://localhost:8080/api/admin/stats/summary
```

### **🔍 Verificar en Frontend:**
1. **Panel de administrador** muestra notificaciones
2. **Eliminar notificación** funciona sin errores
3. **Marcar como leída** funciona correctamente
4. **WebSocket** conecta sin errores
5. **Notificaciones en tiempo real** llegan

---

## 🚀 **Beneficios de las Correcciones:**

- ✅ **Eliminación de errores 500** en el frontend
- ✅ **Notificaciones en tiempo real** funcionando
- ✅ **Panel de administrador** completamente funcional
- ✅ **Consistencia** entre frontend y backend
- ✅ **Mejor experiencia de usuario**

---

## 📞 **Soporte:**

Si encuentras problemas después de aplicar estas correcciones:

1. **Verificar logs del backend** para errores específicos
2. **Revisar Network tab** en DevTools para ver requests
3. **Probar endpoints directamente** con curl/Postman
4. **Verificar configuración CORS** si hay problemas de origen 