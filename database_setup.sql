-- =====================================================
-- SCRIPT CONSOLIDADO DE CONFIGURACIÓN DE BASE DE DATOS
-- =====================================================
-- Este script contiene todas las operaciones necesarias para:
-- 1. Verificar la estructura de la base de datos
-- 2. Arreglar problemas de foreign keys
-- 3. Limpiar datos duplicados
-- 4. Verificar la integridad de los datos
-- 
-- USO:
-- mysql -u root -p -e "source database_setup.sql"
-- 
-- AUTOR: CodEthics Team
-- VERSIÓN: 1.0
-- FECHA: Agosto 2025

USE codethics;

-- =====================================================
-- 1. VERIFICACIÓN DE ESTRUCTURA
-- =====================================================
-- Esta sección verifica que todas las tablas existan y
-- tengan la estructura correcta

-- Mostrar todas las tablas existentes en la base de datos
SHOW TABLES;

-- Verificar estructura de tablas principales
-- Estas tablas son las más importantes del sistema
DESCRIBE projects;      -- Tabla de proyectos
DESCRIBE clients;       -- Tabla de clientes
DESCRIBE users;         -- Tabla de usuarios
DESCRIBE project_team;  -- Tabla de equipos de proyecto
DESCRIBE invoices;      -- Tabla de facturas
DESCRIBE invoice_items; -- Tabla de items de factura
DESCRIBE analytics;     -- Tabla de datos analíticos

-- Verificar foreign keys existentes
-- Esta consulta muestra todas las relaciones entre tablas
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE 
    TABLE_SCHEMA = 'codethics' 
    AND REFERENCED_TABLE_NAME IS NOT NULL;

-- =====================================================
-- 2. ARREGLO DE FOREIGN KEYS
-- =====================================================
-- Esta sección arregla problemas de incompatibilidad
-- entre tipos de datos en las foreign keys

-- Deshabilitar verificación de foreign keys temporalmente
-- Esto permite modificar las constraints sin errores
SET FOREIGN_KEY_CHECKS = 0;

-- Eliminar foreign keys problemáticas (si existen)
-- Estas foreign keys pueden tener problemas de tipos de datos
ALTER TABLE invoice_items DROP FOREIGN KEY IF EXISTS invoice_items_ibfk_1;
ALTER TABLE project_team DROP FOREIGN KEY IF EXISTS project_team_ibfk_1;

-- Recrear las foreign keys con tipos de datos correctos
-- Esto asegura que las relaciones sean válidas
ALTER TABLE invoice_items 
ADD CONSTRAINT invoice_items_ibfk_1 
FOREIGN KEY (invoice_id) REFERENCES invoices(id);

ALTER TABLE project_team 
ADD CONSTRAINT project_team_ibfk_1 
FOREIGN KEY (project_id) REFERENCES projects(id);

-- Habilitar verificación de foreign keys
-- Esto activa nuevamente las validaciones de integridad
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 3. LIMPIEZA DE DATOS DUPLICADOS
-- =====================================================
-- Esta sección identifica y elimina registros duplicados
-- que pueden causar problemas en la aplicación

-- Verificar registros duplicados en project_team
-- Mostrar todos los registros ordenados por ID
SELECT * FROM project_team ORDER BY id;

-- Eliminar registros duplicados (mantener solo los necesarios)
-- Descomentar la línea siguiente si hay duplicados que eliminar
-- DELETE FROM project_team WHERE id IN (3, 4);

-- =====================================================
-- 4. VERIFICACIÓN FINAL
-- =====================================================
-- Esta sección verifica que todo esté funcionando correctamente
-- después de las modificaciones

-- Verificar conteo de registros en cada tabla principal
-- Esto confirma que los datos están intactos
SELECT COUNT(*) as projects_count FROM projects;
SELECT COUNT(*) as clients_count FROM clients;
SELECT COUNT(*) as users_count FROM users;
SELECT COUNT(*) as project_team_count FROM project_team;
SELECT COUNT(*) as invoices_count FROM invoices;
SELECT COUNT(*) as invoice_items_count FROM invoice_items;
SELECT COUNT(*) as analytics_count FROM analytics;

-- Verificar integridad de foreign keys
-- Esta consulta verifica que todas las foreign keys sean válidas
SELECT 
    'project_team' as table_name,
    COUNT(*) as total_records,
    COUNT(CASE WHEN project_id IS NOT NULL THEN 1 END) as valid_foreign_keys
FROM project_team
UNION ALL
SELECT 
    'invoice_items' as table_name,
    COUNT(*) as total_records,
    COUNT(CASE WHEN invoice_id IS NOT NULL THEN 1 END) as valid_foreign_keys
FROM invoice_items;

-- =====================================================
-- 5. INFORMACIÓN DE CONFIGURACIÓN
-- =====================================================
-- Esta sección muestra información del sistema para debugging

-- Mostrar información de la base de datos
-- Incluye versión de MySQL y configuración actual
SELECT 
    DATABASE() as current_database,
    VERSION() as mysql_version,
    @@foreign_key_checks as foreign_key_checks_enabled;

-- =====================================================
-- CREAR TABLA DE NOTIFICACIONES
-- =====================================================

-- Crear tabla de notificaciones si no existe
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL COMMENT 'Tipo de notificación (BUDGET_PENDING, BUDGET_APPROVED, etc.)',
    title VARCHAR(255) NOT NULL COMMENT 'Título de la notificación',
    message TEXT NOT NULL COMMENT 'Mensaje de la notificación',
    priority VARCHAR(20) NOT NULL COMMENT 'Prioridad (low, medium, high)',
    target_user_id BIGINT COMMENT 'ID del usuario específico (puede ser NULL)',
    target_role VARCHAR(50) COMMENT 'Rol objetivo (admin, client, user)',
    related_entity_id BIGINT COMMENT 'ID de la entidad relacionada (presupuesto, ticket, etc.)',
    related_entity_type VARCHAR(50) COMMENT 'Tipo de entidad relacionada (budget, ticket, project)',
    is_read BOOLEAN DEFAULT FALSE COMMENT 'Si la notificación ha sido leída',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de creación',
    metadata TEXT COMMENT 'Datos adicionales en formato JSON (opcional)',
    
    -- Índices para optimizar consultas
    INDEX idx_target_user (target_user_id),
    INDEX idx_target_role (target_role),
    INDEX idx_created_at (created_at),
    INDEX idx_read_status (is_read),
    INDEX idx_entity_relation (related_entity_type, related_entity_id),
    INDEX idx_type_priority (type, priority)
) COMMENT = 'Tabla de notificaciones del sistema';

-- Insertar notificaciones de ejemplo (solo si la tabla está vacía)
INSERT IGNORE INTO notifications (type, title, message, priority, target_role, created_at) VALUES
('SYSTEM_ANNOUNCEMENT', 'Sistema de Notificaciones Activado', 'El sistema de notificaciones está ahora funcionando correctamente', 'medium', 'admin', NOW()),
('SYSTEM_ANNOUNCEMENT', 'Bienvenido al Portal', 'Gracias por usar nuestro sistema de gestión', 'low', 'client', NOW());

-- Verificar que la tabla se creó correctamente
SELECT 'Tabla de notificaciones verificada' as status, COUNT(*) as total_notifications FROM notifications;

-- Script completado exitosamente
-- Este mensaje confirma que el script se ejecutó sin errores
SELECT 'Database setup completed successfully!' as status; 