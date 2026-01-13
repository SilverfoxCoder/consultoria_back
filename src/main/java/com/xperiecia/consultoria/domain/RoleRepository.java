package com.xperiecia.consultoria.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Búsquedas básicas
    Optional<Role> findByName(String name);
    List<Role> findByIsActive(Boolean isActive);
    List<Role> findByNameContainingIgnoreCase(String name);

    // Consultas JPQL personalizadas
    @Query("SELECT r FROM Role r WHERE r.isActive = true")
    List<Role> findAllActiveRoles();

    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    List<Role> findRolesByPermissionId(@Param("permissionId") Long permissionId);

    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Role r WHERE r.isActive = true")
    Long countActiveRoles();

    @Query("SELECT r FROM Role r WHERE r.name LIKE %:name% AND r.isActive = true")
    List<Role> findActiveRolesByNameContaining(@Param("name") String name);

    // Verificar si existe
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
} 
