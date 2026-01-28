CREATE TABLE prospects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(255),
    phone VARCHAR(255),
    status VARCHAR(50),
    found_at DATETIME,
    source VARCHAR(255),
    website VARCHAR(255)
);
