package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Búsquedas básicas
    Optional<Permission> findByName(String name);
    List<Permission> findByResource(String resource);
    List<Permission> findByAction(String action);
    List<Permission> findByIsActive(Boolean isActive);
    List<Permission> findByResourceAndAction(String resource, String action);

    // Consultas JPQL personalizadas
    @Query("SELECT p FROM Permission p WHERE p.isActive = true")
    List<Permission> findAllActivePermissions();

    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.isActive = true")
    List<Permission> findActivePermissionsByResource(@Param("resource") String resource);

    @Query("SELECT p FROM Permission p WHERE p.action = :action AND p.isActive = true")
    List<Permission> findActivePermissionsByAction(@Param("action") String action);

    @Query("SELECT COUNT(p) FROM Permission p WHERE p.isActive = true")
    Long countActivePermissions();

    @Query("SELECT p FROM Permission p WHERE p.name LIKE %:name% AND p.isActive = true")
    List<Permission> findActivePermissionsByNameContaining(@Param("name") String name);

    @Query("SELECT DISTINCT p.resource FROM Permission p WHERE p.isActive = true")
    List<String> findAllActiveResources();

    @Query("SELECT DISTINCT p.action FROM Permission p WHERE p.isActive = true")
    List<String> findAllActiveActions();

    // Verificar si existe
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsByResourceAndAction(String resource, String action);
} 
