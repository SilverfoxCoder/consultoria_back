package com.xperiecia.consultoria.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prospects")
public class Prospect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    private String address;
    private String city;
    private String phone;

    // NEW, CONTACTED, CONVERTED, DISCARDED
    private String status;

    @Column(name = "found_at")
    private LocalDateTime foundAt;

    private String source;

    // URL found or empty if confirmed no-website (or just the query used)
    private String website;

    public Prospect() {
    }

    public Prospect(String companyName, String address, String city, String phone, String status, LocalDateTime foundAt,
            String source) {
        this.companyName = companyName;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.status = status;
        this.foundAt = foundAt;
        this.source = source;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFoundAt() {
        return foundAt;
    }

    public void setFoundAt(LocalDateTime foundAt) {
        this.foundAt = foundAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
