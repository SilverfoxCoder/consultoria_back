package com.xperiecia.consultoria.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Obtiene notificaciones por usuario o rol con paginación
     */
    @Query("SELECT n FROM Notification n WHERE " +
            "(n.targetUserId = :userId OR (n.targetUserId IS NULL AND :userRole = n.targetRole)) " +
            "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdOrRole(@Param("userId") Long userId,
            @Param("userRole") String userRole,
            Pageable pageable);

    /**
     * Cuenta notificaciones no leídas por usuario o rol
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE " +
            "(n.targetUserId = :userId OR (n.targetUserId IS NULL AND :userRole = n.targetRole)) " +
            "AND n.read = false")
    Long countUnreadByUserIdOrRole(@Param("userId") Long userId, @Param("userRole") String userRole);

    /**
     * Marca todas las notificaciones como leídas para un usuario o rol
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE " +
            "(n.targetUserId = :userId OR (n.targetUserId IS NULL AND :userRole = n.targetRole))")
    void markAllAsReadByUserIdOrRole(@Param("userId") Long userId, @Param("userRole") String userRole);

    /**
     * Obtiene notificaciones por tipo
     */
    @Query("SELECT n FROM Notification n WHERE n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findByType(@Param("type") String type);

    /**
     * Obtiene notificaciones por prioridad
     */
    @Query("SELECT n FROM Notification n WHERE n.priority = :priority ORDER BY n.createdAt DESC")
    List<Notification> findByPriority(@Param("priority") String priority);

    /**
     * Obtiene notificaciones por entidad relacionada
     */
    @Query("SELECT n FROM Notification n WHERE n.relatedEntityType = :entityType AND n.relatedEntityId = :entityId")
    List<Notification> findByRelatedEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    /**
     * Obtiene notificaciones recientes (últimas 24 horas)
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("since") java.time.LocalDateTime since);

    /**
     * Obtiene estadísticas de notificaciones por usuario
     */
    @Query("SELECT " +
            "COUNT(n) as total, " +
            "SUM(CASE WHEN n.read = false THEN 1 ELSE 0 END) as unread, " +
            "SUM(CASE WHEN n.priority = 'high' THEN 1 ELSE 0 END) as highPriority " +
            "FROM Notification n WHERE " +
            "(n.targetUserId = :userId OR (n.targetUserId IS NULL AND :userRole = n.targetRole))")
    Object getNotificationStats(@Param("userId") Long userId, @Param("userRole") String userRole);
    
    /**
     * Buscar notificaciones por rol con paginación
     */
    Page<Notification> findByTargetRole(String targetRole, Pageable pageable);
    
    /**
     * Contar notificaciones por rol y estado de lectura
     */
    long countByTargetRoleAndRead(String targetRole, boolean read);
    
    /**
     * Obtener todas las notificaciones por rol
     */
    List<Notification> findByTargetRole(String targetRole);
    
    /**
     * Eliminar todas las notificaciones por rol
     */
    @Modifying
    @Transactional
    void deleteByTargetRole(String targetRole);
}
