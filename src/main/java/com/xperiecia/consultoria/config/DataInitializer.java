package com.xperiecia.consultoria.config;

import com.xperiecia.consultoria.domain.User;
import com.xperiecia.consultoria.domain.UserRepository;
import com.xperiecia.consultoria.domain.Role;
import com.xperiecia.consultoria.domain.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🔧 DataInitializer: Verificando usuario administrador...");

        // 1. Buscar si existe el usuario antiguo
        Optional<User> oldAdmin = userRepository.findByEmail("admin@codexcore.com");
        if (oldAdmin.isPresent()) {
            User admin = oldAdmin.get();
            admin.setEmail("admin@xperiecia.com");
            admin.setRole("admin"); // Asegurar que tenga el rol admin string
            
            // Asegurar que tenga el rol en la relación ManyToMany
            Optional<Role> adminRoleOpt = roleRepository.findByName("Administrador");
            if (adminRoleOpt.isPresent()) {
                admin.addRole(adminRoleOpt.get());
            }

            userRepository.save(admin);
            System.out.println("✅ DataInitializer: Admin actualizado (email y roles)");
        }

        // 2. Verificar o crear el usuario admin correcto
        Optional<User> adminOpt = userRepository.findByEmail("admin@xperiecia.com");
        User admin;
        if (adminOpt.isEmpty()) {
            admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@xperiecia.com");
            admin.setPasswordHash("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"); // admin123
            admin.setRole("admin");
            admin.setStatus("active");
            admin.setPhone("123456789");
            userRepository.save(admin);
            System.out.println("✅ DataInitializer: Usuario administrador creado desde cero");
        } else {
            admin = adminOpt.get();
            // Force update role only if it's not admin
            if (!"admin".equals(admin.getRole())) {
                admin.setRole("admin");
                userRepository.save(admin);
                System.out.println("✅ DataInitializer: Rol corregido a 'admin' para usuario existente");
            } else {
                System.out.println("ℹ️  DataInitializer: Usuario administrador ya tiene rol correcto");
            }
        }


        // 3. Asegurar que tenga el rol de Administrador en la tabla user_roles
        Optional<User> currentAdminOpt = userRepository.findByEmail("admin@xperiecia.com");
        if (currentAdminOpt.isPresent()) {
            User currentAdmin = currentAdminOpt.get();
            Optional<Role> adminRoleOpt = roleRepository.findByName("Administrador");
            
            if (adminRoleOpt.isPresent()) {
                boolean hasRole = currentAdmin.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("Administrador"));
                
                if (!hasRole) {
                    currentAdmin.addRole(adminRoleOpt.get());
                    userRepository.save(currentAdmin);
                    System.out.println("✅ DataInitializer: Rol 'Administrador' asignado explícitamente");
                }
            } else {
                 System.out.println("⚠️ DataInitializer: Rol 'Administrador' no encontrado en DB");
            }
        }
        
        System.out.println("🔧 DataInitializer: Verificación completa.");
    }
}
