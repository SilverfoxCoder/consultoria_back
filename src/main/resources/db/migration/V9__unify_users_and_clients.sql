-- =====================================================
-- MIGRATION: UNIFY USERS AND CLIENTS
-- =====================================================

-- 1. Add missing columns to 'users' table
ALTER TABLE users ADD COLUMN company VARCHAR(100);
ALTER TABLE users ADD COLUMN industry VARCHAR(50);
ALTER TABLE users ADD COLUMN address VARCHAR(255);
ALTER TABLE users ADD COLUMN website VARCHAR(100);
ALTER TABLE users ADD COLUMN notes TEXT;
ALTER TABLE users ADD COLUMN last_contact DATETIME;
ALTER TABLE users ADD COLUMN total_revenue DECIMAL(15,2) DEFAULT 0.00;
ALTER TABLE users ADD COLUMN total_projects INT DEFAULT 0;

-- Temporary column to map old client IDs to new user IDs
ALTER TABLE users ADD COLUMN old_client_id BIGINT;

-- 2. Migrate data from 'clients' to 'users'
INSERT INTO users (
    name, email, password_hash, role, phone, 
    company, industry, address, website, notes, 
    last_contact, total_revenue, total_projects, 
    status, registered_at, old_client_id
)
SELECT 
    COALESCE(NULLIF(contact_person, ''), name),
    email, 
    '$2a$10$DUMMYHASHFORCLIENTMIGRATION...........',
    'CLIENT', 
    phone,
    COALESCE(NULLIF(company, ''), name),
    industry, 
    address, 
    website, 
    notes, 
    CAST(last_contact AS DATETIME),
    total_revenue, 
    total_projects, 
    COALESCE(status, 'ACTIVO'),
    NOW(),
    id
FROM clients;

-- 3. Update Foreign Keys
-- Note: Assuming standard MySQL FK naming conventions or implicit creation from V1

-- PROJECTS
-- Drop old FK if exists (Handling typical MySQL names)
SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'projects' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
SET @sql := IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE projects DROP FOREIGN KEY ', @fk_name), 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update IDs
UPDATE projects p 
JOIN users u ON u.old_client_id = p.client_id 
SET p.client_id = u.id 
WHERE p.client_id IS NOT NULL;

-- Add new FK
ALTER TABLE projects ADD CONSTRAINT fk_projects_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE SET NULL;


-- INVOICES
SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'invoices' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
SET @sql := IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE invoices DROP FOREIGN KEY ', @fk_name), 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE invoices i 
JOIN users u ON u.old_client_id = i.client_id 
SET i.client_id = u.id 
WHERE i.client_id IS NOT NULL;

ALTER TABLE invoices ADD CONSTRAINT fk_invoices_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE SET NULL;


-- ANALYTICS
SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'analytics' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
SET @sql := IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE analytics DROP FOREIGN KEY ', @fk_name), 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE analytics a 
JOIN users u ON u.old_client_id = a.client_id 
SET a.client_id = u.id 
WHERE a.client_id IS NOT NULL;

ALTER TABLE analytics ADD CONSTRAINT fk_analytics_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;


-- BUDGETS
SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'budgets' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
SET @sql := IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE budgets DROP FOREIGN KEY ', @fk_name), 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE budgets b 
JOIN users u ON u.old_client_id = b.client_id 
SET b.client_id = u.id 
WHERE b.client_id IS NOT NULL;

ALTER TABLE budgets ADD CONSTRAINT fk_budgets_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;


-- 4. Clean up
-- SERVICES
SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'services' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
SET @sql := IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE services DROP FOREIGN KEY ', @fk_name), 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE services s 
JOIN users u ON u.old_client_id = s.client_id 
SET s.client_id = u.id 
WHERE s.client_id IS NOT NULL;

ALTER TABLE services ADD CONSTRAINT fk_services_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;


-- SUPPORT TICKETS
SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'support_tickets' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
SET @sql := IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE support_tickets DROP FOREIGN KEY ', @fk_name), 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE support_tickets t 
JOIN users u ON u.old_client_id = t.client_id 
SET t.client_id = u.id 
WHERE t.client_id IS NOT NULL;

ALTER TABLE support_tickets ADD CONSTRAINT fk_tickets_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;


-- 5. Drop table
ALTER TABLE users DROP COLUMN old_client_id;
DROP TABLE clients;
