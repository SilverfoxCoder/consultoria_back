CREATE TABLE IF NOT EXISTS project_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    path VARCHAR(500) NOT NULL,
    uploaded_at DATETIME,
    uploaded_by BIGINT
);
