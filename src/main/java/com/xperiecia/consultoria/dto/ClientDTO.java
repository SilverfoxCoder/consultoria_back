package com.xperiecia.consultoria.dto;

// import com.xperiecia.consultoria.domain.Client; // Removed
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
    // Constructor from Entity (User acting as Client)
    public static ClientDTO fromEntity(com.xperiecia.consultoria.domain.User user) {
        ClientDTO dto = new ClientDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        // User uses 'name' as contact person implicitly or we use name
        dto.setContactPerson(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCompany(user.getCompany());
        dto.setIndustry(user.getIndustry());
        dto.setStatus(user.getStatus());
        dto.setAddress(user.getAddress());
        dto.setWebsite(user.getWebsite());
        dto.setNotes(user.getNotes());
        // dto.setLastContact(user.getLastContact()); // User entity might need
        // LocalDateTime -> LocalDate conversion if types differ
        // Assuming User has LocalDateTime lastContact, ClientDTO has LocalDate
        if (user.getLastContact() != null) {
            dto.setLastContact(user.getLastContact().toLocalDate());
        }
        dto.setTotalRevenue(user.getTotalRevenue());
        dto.setTotalProjects(user.getTotalProjects());
        return dto;
    }

    // Método para convertir DTO a Entity (User)
    public com.xperiecia.consultoria.domain.User toEntity() {
        com.xperiecia.consultoria.domain.User user = new com.xperiecia.consultoria.domain.User();
        if (this.id != null)
            user.setId(this.id);
        user.setName(this.name);
        // user.setContactPerson(this.contactPerson); // User doesn't have this, uses
        // name
        if (this.email != null && !this.email.trim().isEmpty()) {
            user.setEmail(this.email);
        } else {
            user.setEmail(null);
        }
        user.setPhone(this.phone);
        user.setCompany(this.company);
        user.setIndustry(this.industry);
        user.setStatus(this.status);
        user.setAddress(this.address);
        user.setWebsite(this.website);
        user.setNotes(this.notes);
        if (this.lastContact != null) {
            user.setLastContact(this.lastContact.atStartOfDay());
        }
        user.setTotalRevenue(this.totalRevenue);
        user.setTotalProjects(this.totalProjects);
        user.setRole("CLIENT"); // Default role
        return user;
    }
}
