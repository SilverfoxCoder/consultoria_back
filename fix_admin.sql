USE codethics;
UPDATE users SET email = 'admin@xperiecia.com' WHERE email = 'admin@codexcore.com';
INSERT IGNORE INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'admin@xperiecia.com' AND r.name = 'Administrador';
SELECT 'Admin fixed' as status;
