-- =====================================================
-- MIGRACIÓN INICIAL - ESTRUCTURA BASE DE DATOS
-- =====================================================
-- Este archivo crea la estructura inicial de la base de datos
-- incluyendo todas las tablas principales del sistema
-- 
-- AUTOR: Xperiecia Team
-- VERSIÓN: 1.0
-- FECHA: Agosto 2025

-- =====================================================
-- 1. TABLA DE USUARIOS
-- =====================================================
-- Almacena información de usuarios del sistema
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'user',
    phone VARCHAR(30),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'active'
);

-- Datos de prueba para usuarios
-- Contraseña: admin123 (hash bcrypt)
INSERT INTO users (name, email, password_hash, role, phone, status) VALUES
('Administrador', 'admin@code.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin', '123456789', 'active'),
('Cliente Demo', 'cliente@empresa.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'client', '987654321', 'active');

-- =====================================================
-- 2. TABLA DE CLIENTES
-- =====================================================
-- Almacena información de clientes de la consultoría
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(30),
    company VARCHAR(100),
    industry VARCHAR(50),
    status VARCHAR(20) DEFAULT 'Prospecto',
    address VARCHAR(255),
    website VARCHAR(100),
    notes TEXT,
    last_contact DATE,
    total_revenue DECIMAL(15,2) DEFAULT 0.00,
    total_projects INT DEFAULT 0
);

-- =====================================================
-- 3. TABLA DE PROYECTOS
-- =====================================================
-- Almacena información de proyectos de consultoría
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    client_id BIGINT,
    status ENUM('PLANIFICACION', 'EN_PROGRESO', 'COMPLETADO', 'CANCELADO', 'PAUSADO') DEFAULT 'PLANIFICACION',
    progress INT DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    budget DECIMAL(15,2) DEFAULT 0.00,
    spent DECIMAL(15,2) DEFAULT 0.00,
    priority ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') DEFAULT 'MEDIA',
    description TEXT,
    jira_enabled BOOLEAN DEFAULT FALSE,
    jira_url VARCHAR(255),
    jira_project_key VARCHAR(50),
    jira_board_id VARCHAR(50),
    jira_last_sync TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE SET NULL
);

-- =====================================================
-- 4. TABLA DE EQUIPOS DE PROYECTO
-- =====================================================
-- Almacena miembros del equipo de cada proyecto
CREATE TABLE project_team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- =====================================================
-- 5. TABLA DE TAREAS
-- =====================================================
-- Almacena tareas de los proyectos
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    project_id BIGINT,
    assigned_to_id BIGINT,
    status ENUM('Pendiente', 'En Progreso', 'Completada', 'Cancelada', 'Pausada') DEFAULT 'Pendiente',
    priority ENUM('Baja', 'Media', 'Alta', 'Crítica') DEFAULT 'Media',
    estimated_hours DECIMAL(10,2),
    actual_hours DECIMAL(10,2),
    start_date DATE,
    due_date DATE,
    completed_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =====================================================
-- 6. TABLA DE ROLES
-- =====================================================
-- Almacena roles del sistema para control de acceso
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- 7. TABLA DE PERMISOS
-- =====================================================
-- Almacena permisos específicos del sistema
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_resource_action (resource, action)
);

-- =====================================================
-- 8. TABLA DE RELACIÓN ROLES-PERMISOS
-- =====================================================
-- Tabla intermedia para relacionar roles con permisos
CREATE TABLE role_permissions (
    role_id BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- =====================================================
-- 9. TABLA DE RELACIÓN USUARIOS-ROLES
-- =====================================================
-- Tabla intermedia para relacionar usuarios con roles
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =====================================================
-- 10. TABLA DE ENTRADAS DE TIEMPO
-- =====================================================
-- Almacena registros de tiempo trabajado
CREATE TABLE time_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT,
    task_id BIGINT,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_hours DECIMAL(10,2),
    description TEXT,
    status ENUM('Pendiente', 'Aprobado', 'Rechazado', 'En Revisión') DEFAULT 'Pendiente',
    billable BOOLEAN DEFAULT FALSE,
    billing_rate DECIMAL(10,2),
    total_amount DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
);

-- =====================================================
-- 11. TABLA DE FACTURAS
-- =====================================================
-- Almacena información de facturas
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT,
    number VARCHAR(50) UNIQUE,
    status ENUM('BORRADOR', 'ENVIADA', 'PAGADA', 'VENCIDA', 'CANCELADA') DEFAULT 'BORRADOR',
    issued_at DATE,
    paid_at DATE,
    amount DECIMAL(15,2) DEFAULT 0.00,
    payment_terms VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE SET NULL
);

-- =====================================================
-- 12. TABLA DE ITEMS DE FACTURA
-- =====================================================
-- Almacena items individuales de cada factura
CREATE TABLE invoice_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(15,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    type ENUM('SERVICIO', 'PRODUCTO', 'HORA', 'MATERIAL', 'OTRO') DEFAULT 'SERVICIO',
    status ENUM('ACTIVO', 'CANCELADO', 'DEVUELTO', 'PENDIENTE') DEFAULT 'ACTIVO',
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    tax_amount DECIMAL(15,2) DEFAULT 0.00,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    discount_amount DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

-- =====================================================
-- 13. TABLA DE ANALÍTICAS
-- =====================================================
-- Almacena datos analíticos y KPIs
CREATE TABLE analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT,
    month_period VARCHAR(7) NOT NULL, -- Formato: YYYY-MM
    total_spent DECIMAL(15,2) DEFAULT 0.00,
    active_projects INT DEFAULT 0,
    open_tickets INT DEFAULT 0,
    avg_response_time DECIMAL(21,0) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- =====================================================
-- 14. TABLA DE CONFIGURACIÓN DE EMPRESA
-- =====================================================
-- Almacena configuración general de la empresa
CREATE TABLE company_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    logo VARCHAR(255),
    address VARCHAR(255),
    phone VARCHAR(30),
    email VARCHAR(100),
    tax_id VARCHAR(30),
    website VARCHAR(100),
    linkedin VARCHAR(100),
    twitter VARCHAR(100),
    facebook VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- DATOS INICIALES
-- =====================================================

-- Insertar roles básicos
INSERT INTO roles (name, description) VALUES
('Administrador', 'Acceso completo al sistema'),
('Gerente de Proyecto', 'Gestión de proyectos y equipos'),
('Desarrollador', 'Desarrollo y mantenimiento'),
('Cliente', 'Acceso limitado a proyectos propios');

-- Insertar permisos básicos
INSERT INTO permissions (name, description, resource, action) VALUES
('Ver Proyectos', 'Ver lista de proyectos', 'projects', 'read'),
('Crear Proyectos', 'Crear nuevos proyectos', 'projects', 'create'),
('Editar Proyectos', 'Modificar proyectos existentes', 'projects', 'update'),
('Eliminar Proyectos', 'Eliminar proyectos', 'projects', 'delete'),
('Ver Tareas', 'Ver lista de tareas', 'tasks', 'read'),
('Crear Tareas', 'Crear nuevas tareas', 'tasks', 'create'),
('Editar Tareas', 'Modificar tareas existentes', 'tasks', 'update'),
('Eliminar Tareas', 'Eliminar tareas', 'tasks', 'delete'),
('Ver Roles', 'Ver lista de roles', 'roles', 'read'),
('Gestionar Roles', 'Crear, editar y eliminar roles', 'roles', 'manage'),
('Ver Permisos', 'Ver lista de permisos', 'permissions', 'read'),
('Gestionar Permisos', 'Crear, editar y eliminar permisos', 'permissions', 'manage'),
('Ver Entradas de Tiempo', 'Ver entradas de tiempo', 'time_entries', 'read'),
('Crear Entradas de Tiempo', 'Crear nuevas entradas de tiempo', 'time_entries', 'create'),
('Editar Entradas de Tiempo', 'Modificar entradas de tiempo', 'time_entries', 'update'),
('Eliminar Entradas de Tiempo', 'Eliminar entradas de tiempo', 'time_entries', 'delete');

-- Asignar todos los permisos al rol de Administrador
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Administrador';

-- Asignar permisos específicos a otros roles
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Gerente de Proyecto' 
AND p.resource IN ('projects', 'tasks', 'time_entries');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Desarrollador' 
AND p.resource IN ('tasks', 'time_entries');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Cliente' 
AND p.resource = 'projects' AND p.action = 'read';

-- Asignar roles a usuarios existentes
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r 
WHERE u.email = 'admin@xperiecia.com' AND r.name = 'Administrador';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r 
WHERE u.email = 'cliente@empresa.com' AND r.name = 'Cliente'; 

-- =====================================================
-- TABLA: budgets (Presupuestos)
-- =====================================================
-- Tabla para gestionar presupuestos de clientes
CREATE TABLE budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    service_type VARCHAR(100) NOT NULL,
    budget DECIMAL(15,2) NOT NULL,
    timeline VARCHAR(100),
    additional_info TEXT,
    client_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    response_date TIMESTAMP NULL,
    response_notes TEXT,
    approved_budget DECIMAL(15,2),
    approved_timeline VARCHAR(100),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Comentarios sobre la tabla budgets
-- Esta tabla almacena las solicitudes de presupuesto que los clientes
-- envían a través del frontend, incluyendo detalles del proyecto,
-- presupuesto estimado y timeline.
-- 
-- Campos principales:
-- - title: Título del proyecto
-- - description: Descripción detallada del proyecto
-- - service_type: Tipo de servicio solicitado
-- - budget: Presupuesto estimado por el cliente
-- - timeline: Timeline estimado del proyecto
-- - additional_info: Información adicional del proyecto
-- - status: Estado del presupuesto (PENDIENTE, EN_REVISION, APROBADO, RECHAZADO, CANCELADO)
-- - response_date: Fecha de respuesta del administrador
-- - response_notes: Notas de respuesta del administrador
-- - approved_budget: Presupuesto aprobado (si aplica)
-- - approved_timeline: Timeline aprobado (si aplica) 