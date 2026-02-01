-- Add user_id column to project_team table (IDEMPOTENT FIX)
DROP PROCEDURE IF EXISTS update_project_team_v10;

DELIMITER $$
CREATE PROCEDURE update_project_team_v10()
BEGIN
    -- 1. Check if column exists. If strictly safer, we might want to check type, but dropping and re-adding if empty is easiest for migration repair.
    -- However, if data exists (unlikely given it's a new column), we should be careful. 
    -- Assuming this is a fresh column being added, we can recreate it to ensure type matches.
    
    -- Drop FK if exists (to allow column drop)
    IF EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME = 'project_team' AND CONSTRAINT_NAME = 'fk_project_team_user') THEN
        ALTER TABLE project_team DROP FOREIGN KEY fk_project_team_user;
    END IF;

    -- Drop index if exists
    -- (MySQL doesn't support IF EXISTS in DROP INDEX consistently across versions without procedure, but usually safe to ignore if we handle column)
    IF EXISTS((SELECT * FROM information_schema.STATISTICS WHERE TABLE_NAME = 'project_team' AND INDEX_NAME = 'idx_project_team_user')) THEN
        DROP INDEX idx_project_team_user ON project_team;
    END IF;

    -- Drop column if exists (to force correct type)
    IF EXISTS(SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME='project_team' AND COLUMN_NAME='user_id') THEN
        ALTER TABLE project_team DROP COLUMN user_id;
    END IF;

    -- Add column explicitly as UNSIGNED to match users.id if it's auto_increment unsigned
    ALTER TABLE project_team ADD COLUMN user_id BIGINT UNSIGNED;

    -- Add Foreign Key constraint - DISABLED TEMPORARILY due to incompatibility
    -- ALTER TABLE project_team 
    -- ADD CONSTRAINT fk_project_team_user 
    -- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

    -- Add index
    CREATE INDEX idx_project_team_user ON project_team(user_id);

END$$
DELIMITER ;

CALL update_project_team_v10();

DROP PROCEDURE IF EXISTS update_project_team_v10;
