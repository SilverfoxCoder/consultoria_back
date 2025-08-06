package com.codethics.consultoria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "language")
    private String language;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "currency")
    private String currency;

    @Column(name = "notifications_email")
    private Boolean notificationsEmail = true;

    @Column(name = "notifications_sms")
    private Boolean notificationsSms = false;

    @Column(name = "notifications_push")
    private Boolean notificationsPush = true;
}