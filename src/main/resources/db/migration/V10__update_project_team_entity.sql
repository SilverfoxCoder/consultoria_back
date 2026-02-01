-- Add user_id column to project_team table
ALTER TABLE project_team ADD COLUMN user_id BIGINT;

-- Add Foreign Key constraint
ALTER TABLE project_team
ADD CONSTRAINT fk_project_team_user
FOREIGN KEY (user_id) REFERENCES users(id);

-- Optional: Add index for performance
CREATE INDEX idx_project_team_user ON project_team(user_id);
