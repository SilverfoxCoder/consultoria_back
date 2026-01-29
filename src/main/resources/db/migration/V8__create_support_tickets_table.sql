-- Create support_tickets table
CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(255),
    created_at TIMESTAMP,
    closed_at TIMESTAMP,
    priority VARCHAR(255),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);
