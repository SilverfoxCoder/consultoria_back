package com.codethics.consultoria.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(name = "contact_person")
    private String contactPerson;
    private String email;
    private String phone;
    private String company;
    private String industry;
    private String status;
    private String address;
    private String website;
    private String notes;
    @Column(name = "last_contact")
    private LocalDate lastContact;
    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;
    @Column(name = "total_projects")
    private Integer totalProjects;

    public Client() {}

    public Client(Long id, String name, String contactPerson, String email, String phone, String company, String industry, String status, String address, String website, String notes, LocalDate lastContact, BigDecimal totalRevenue, Integer totalProjects) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.industry = industry;
        this.status = status;
        this.address = address;
        this.website = website;
        this.notes = notes;
        this.lastContact = lastContact;
        this.totalRevenue = totalRevenue;
        this.totalProjects = totalProjects;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDate getLastContact() { return lastContact; }
    public void setLastContact(LocalDate lastContact) { this.lastContact = lastContact; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public Integer getTotalProjects() { return totalProjects; }
    public void setTotalProjects(Integer totalProjects) { this.totalProjects = totalProjects; }
} 