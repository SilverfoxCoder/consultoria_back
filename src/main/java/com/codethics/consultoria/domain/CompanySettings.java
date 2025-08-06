package com.codethics.consultoria.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "company_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "logo")
    private String logo;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "website")
    private String website;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "facebook")
    private String facebook;
}