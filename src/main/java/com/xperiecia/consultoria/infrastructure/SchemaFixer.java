package com.xperiecia.consultoria.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class SchemaFixer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.err.println("🔧 SchemaFixer (CommandLineRunner): Starting schema verification...");
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Fix 1: user_roles FKs
            try {
                System.err.println("🔧 SchemaFixer: Fixing user_roles FKs...");
                stmt.execute("ALTER TABLE user_roles MODIFY user_id BIGINT");
                stmt.execute("ALTER TABLE user_roles MODIFY role_id BIGINT"); 
                System.err.println("✅ SchemaFixer: user_roles FKs fixed.");
            } catch (Exception e) {
                System.err.println("⚠️ SchemaFixer: Failed in user_roles fix (ignoring): " + e.getMessage());
            }

            // Fix 2: Add assignee column
            try {
                System.err.println("🔧 SchemaFixer: Checking/Adding 'assignee' column to 'tasks' table...");
                stmt.execute("ALTER TABLE tasks ADD COLUMN assignee VARCHAR(255)");
                System.err.println("✅ SchemaFixer: Column 'assignee' added successfully.");
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("Duplicate column")) {
                     System.err.println("ℹ️ SchemaFixer: Column 'assignee' already exists.");
                } else {
                     System.err.println("❌ SchemaFixer: Failed to add column 'assignee'. Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
                }
            }
            
            // Fix 3: Normalize Enums (Legacy Data Fix)
            try {
                System.err.println("🔧 SchemaFixer: Normalizing Task Enums...");
                
                // Status Normalization
                int s1 = stmt.executeUpdate("UPDATE tasks SET status = 'PENDIENTE' WHERE status = 'Pendiente'");
                int s2 = stmt.executeUpdate("UPDATE tasks SET status = 'EN_PROGRESO' WHERE status = 'En Progreso'");
                int s3 = stmt.executeUpdate("UPDATE tasks SET status = 'COMPLETADA' WHERE status IN ('Completada', 'Completado')");
                int s4 = stmt.executeUpdate("UPDATE tasks SET status = 'CANCELADA' WHERE status = 'Cancelada'");
                int s5 = stmt.executeUpdate("UPDATE tasks SET status = 'PAUSADA' WHERE status = 'Pausada'");
                
                // Priority Normalization
                int p1 = stmt.executeUpdate("UPDATE tasks SET priority = 'BAJA' WHERE priority = 'Baja'");
                int p2 = stmt.executeUpdate("UPDATE tasks SET priority = 'MEDIA' WHERE priority = 'Media'");
                int p3 = stmt.executeUpdate("UPDATE tasks SET priority = 'ALTA' WHERE priority = 'Alta'");
                int p4 = stmt.executeUpdate("UPDATE tasks SET priority = 'CRITICA' WHERE priority IN ('Critica', 'Crítica')");

                System.err.println("✅ SchemaFixer: Enums normalized. Status updates: " + (s1+s2+s3+s4+s5) + ", Priority updates: " + (p1+p2+p3+p4));
            } catch (Exception e) {
                 System.err.println("⚠️ SchemaFixer: Failed to normalize enums: " + e.getMessage());
            }

            System.err.println("✅ SchemaFixer: All checks completed.");
            
        } catch (Exception e) {
            System.err.println("⚠️ SchemaFixer: Fatal error connecting to DB: " + e.getMessage());
        }
    }
}
