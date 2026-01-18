package com.xperiecia.consultoria.dto;

import com.xperiecia.consultoria.domain.Client;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String name;
    @JsonAlias("contact_person")
    private String contactPerson;
    private String email;
    private String phone;
    private String company;
    private String industry;
    private String status;
    private String address;
    private String website;
    private String notes;
    @JsonAlias("last_contact")
    private LocalDate lastContact;
    @JsonAlias("total_revenue")
    private BigDecimal totalRevenue;
    @JsonAlias("total_projects")
    private Integer totalProjects;

    // Constructor from Entity
    public static ClientDTO fromEntity(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setContactPerson(client.getContactPerson());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setCompany(client.getCompany());
        dto.setIndustry(client.getIndustry());
        dto.setStatus(client.getStatus());
        dto.setAddress(client.getAddress());
        dto.setWebsite(client.getWebsite());
        dto.setNotes(client.getNotes());
        dto.setLastContact(client.getLastContact());
        dto.setTotalRevenue(client.getTotalRevenue());
        dto.setTotalProjects(client.getTotalProjects());
        return dto;
    }

    // Método para convertir DTO a Entity
    public Client toEntity() {
        Client client = new Client();
        client.setId(this.id);
        client.setName(this.name);
        client.setContactPerson(this.contactPerson);
        client.setEmail(this.email);
        client.setPhone(this.phone);
        client.setCompany(this.company);
        client.setIndustry(this.industry);
        client.setStatus(this.status);
        client.setAddress(this.address);
        client.setWebsite(this.website);
        client.setNotes(this.notes);
        client.setLastContact(this.lastContact);
        client.setTotalRevenue(this.totalRevenue);
        client.setTotalProjects(this.totalProjects);
        return client;
    }
}
