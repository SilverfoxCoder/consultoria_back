-- Add missing columns to projects table (IDEMPOTENT)
DROP PROCEDURE IF EXISTS add_v5_columns;

DELIMITER $$
CREATE PROCEDURE add_v5_columns()
BEGIN
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='projects' AND COLUMN_NAME='efficiency_score') THEN
        ALTER TABLE projects ADD COLUMN efficiency_score FLOAT DEFAULT 0.0;
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='projects' AND COLUMN_NAME='hours_logged') THEN
        ALTER TABLE projects ADD COLUMN hours_logged FLOAT DEFAULT 0.0;
    END IF;
    IF NOT EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='projects' AND COLUMN_NAME='executive_summary') THEN
        ALTER TABLE projects ADD COLUMN executive_summary TEXT;
    END IF;
END$$
DELIMITER ;

CALL add_v5_columns();

DROP PROCEDURE IF EXISTS add_v5_columns;
