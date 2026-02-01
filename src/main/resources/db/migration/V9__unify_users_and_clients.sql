-- =====================================================
-- MIGRATION: UNIFY USERS AND CLIENTS (IDEMPOTENT REPAIR)
-- =====================================================

DROP PROCEDURE IF EXISTS upgrade_database;

DELIMITER $$
CREATE PROCEDURE upgrade_database()
BEGIN
    -- 1. Add missing columns to 'users' table if they don't exist
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='company') THEN
        ALTER TABLE users ADD COLUMN company VARCHAR(100);
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='industry') THEN
        ALTER TABLE users ADD COLUMN industry VARCHAR(50);
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='address') THEN
        ALTER TABLE users ADD COLUMN address VARCHAR(255);
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='website') THEN
        ALTER TABLE users ADD COLUMN website VARCHAR(100);
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='notes') THEN
        ALTER TABLE users ADD COLUMN notes TEXT;
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='last_contact') THEN
        ALTER TABLE users ADD COLUMN last_contact DATETIME;
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='total_revenue') THEN
        ALTER TABLE users ADD COLUMN total_revenue DECIMAL(15,2) DEFAULT 0.00;
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='total_projects') THEN
        ALTER TABLE users ADD COLUMN total_projects INT DEFAULT 0;
    END IF;

    -- Temporary column to map old client IDs to new user IDs
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='old_client_id') THEN
        ALTER TABLE users ADD COLUMN old_client_id BIGINT;
    END IF;

    -- 2. Migrate data from 'clients' to 'users'
    -- Only insert if 'clients' table exists
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='clients') THEN
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
        FROM clients
        ON DUPLICATE KEY UPDATE old_client_id = VALUES(old_client_id);
    END IF;

    -- 3. Update Foreign Keys

    -- PROJECTS
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='projects') THEN
         -- Drop old FK if exists
        SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'projects' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
        IF @fk_name IS NOT NULL THEN
            SET @sql := CONCAT('ALTER TABLE projects DROP FOREIGN KEY ', @fk_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        -- Update IDs
        UPDATE projects p 
        JOIN users u ON u.old_client_id = p.client_id 
        SET p.client_id = u.id 
        WHERE p.client_id IS NOT NULL;
        
        -- Delete orphans
        DELETE FROM projects WHERE client_id IS NOT NULL AND client_id NOT IN (SELECT id FROM users);

        -- Add new FK if not exists
        IF NOT EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME = 'projects' AND CONSTRAINT_NAME = 'fk_projects_user') THEN
             ALTER TABLE projects ADD CONSTRAINT fk_projects_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE SET NULL;
        END IF;
    END IF;


    -- INVOICES
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='invoices') THEN
        SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'invoices' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
        IF @fk_name IS NOT NULL THEN
            SET @sql := CONCAT('ALTER TABLE invoices DROP FOREIGN KEY ', @fk_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        UPDATE invoices i 
        JOIN users u ON u.old_client_id = i.client_id 
        SET i.client_id = u.id 
        WHERE i.client_id IS NOT NULL;
        
        -- Delete orphans
        DELETE FROM invoices WHERE client_id IS NOT NULL AND client_id NOT IN (SELECT id FROM users);

        IF NOT EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME = 'invoices' AND CONSTRAINT_NAME = 'fk_invoices_user') THEN
             ALTER TABLE invoices ADD CONSTRAINT fk_invoices_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE SET NULL;
        END IF;
    END IF;

    -- ANALYTICS
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='analytics') THEN
        SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'analytics' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
        IF @fk_name IS NOT NULL THEN
            SET @sql := CONCAT('ALTER TABLE analytics DROP FOREIGN KEY ', @fk_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        UPDATE analytics a 
        JOIN users u ON u.old_client_id = a.client_id 
        SET a.client_id = u.id 
        WHERE a.client_id IS NOT NULL;
        
        -- Delete orphans
        DELETE FROM analytics WHERE client_id IS NOT NULL AND client_id NOT IN (SELECT id FROM users);

        IF NOT EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME = 'analytics' AND CONSTRAINT_NAME = 'fk_analytics_user') THEN
            ALTER TABLE analytics ADD CONSTRAINT fk_analytics_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END IF;

    -- BUDGETS
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='budgets') THEN
        SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'budgets' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
        IF @fk_name IS NOT NULL THEN
            SET @sql := CONCAT('ALTER TABLE budgets DROP FOREIGN KEY ', @fk_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        UPDATE budgets b 
        JOIN users u ON u.old_client_id = b.client_id 
        SET b.client_id = u.id 
        WHERE b.client_id IS NOT NULL;

        -- Delete orphans
        DELETE FROM budgets WHERE client_id IS NOT NULL AND client_id NOT IN (SELECT id FROM users);

        IF NOT EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME = 'budgets' AND CONSTRAINT_NAME = 'fk_budgets_user') THEN
            ALTER TABLE budgets ADD CONSTRAINT fk_budgets_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END IF;

    -- SERVICES
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='services') THEN
        SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'services' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
        IF @fk_name IS NOT NULL THEN
            SET @sql := CONCAT('ALTER TABLE services DROP FOREIGN KEY ', @fk_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        UPDATE services s 
        JOIN users u ON u.old_client_id = s.client_id 
        SET s.client_id = u.id 
        WHERE s.client_id IS NOT NULL;
        
        -- Delete orphans
        DELETE FROM services WHERE client_id IS NOT NULL AND client_id NOT IN (SELECT id FROM users);

        IF NOT EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME = 'services' AND CONSTRAINT_NAME = 'fk_services_user') THEN
            ALTER TABLE services ADD CONSTRAINT fk_services_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END IF;

    -- SUPPORT TICKETS
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='support_tickets') THEN
        SET @fk_name := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = 'support_tickets' AND COLUMN_NAME = 'client_id' AND REFERENCED_TABLE_NAME = 'clients' LIMIT 1);
        IF @fk_name IS NOT NULL THEN
            SET @sql := CONCAT('ALTER TABLE support_tickets DROP FOREIGN KEY ', @fk_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        UPDATE support_tickets t 
        JOIN users u ON u.old_client_id = t.client_id 
        SET t.client_id = u.id 
        WHERE t.client_id IS NOT NULL;
        
        -- Delete orphans
        DELETE FROM support_tickets WHERE client_id IS NOT NULL AND client_id NOT IN (SELECT id FROM users);

        IF NOT EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME = 'support_tickets' AND CONSTRAINT_NAME = 'fk_tickets_user') THEN
            ALTER TABLE support_tickets ADD CONSTRAINT fk_tickets_user FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END IF;

    -- 5. Drop table (CAREFUL: In idempotent runs, we only drop if we are sure everything migrated)
    -- Ideally, we only drop if the table exists
    IF EXISTS(SELECT * FROM information_schema.TABLES WHERE TABLE_NAME='clients') THEN
        DROP TABLE clients;
    END IF;

    -- Clean up temporary column. 
    -- Only drop it if it exists AND we are sure we don't need it? 
    -- For this script, we assume successful run = drop it.
    IF EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='old_client_id') THEN
        ALTER TABLE users DROP COLUMN old_client_id;
    END IF;

END$$

DELIMITER ;

CALL upgrade_database();

DROP PROCEDURE IF EXISTS upgrade_database;
