-- Add missing columns to projects table
ALTER TABLE projects
ADD COLUMN efficiency_score FLOAT DEFAULT 0.0,
ADD COLUMN hours_logged FLOAT DEFAULT 0.0,
ADD COLUMN executive_summary TEXT;
