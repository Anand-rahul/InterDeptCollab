package com.sharktank.interdepcollab.user.model;

import java.util.Set;
import java.io.Serializable;

import com.sharktank.interdepcollab.requirement.model.Requirement;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser implements Serializable {

    public static enum Role {
        ROLE_ADMIN, ROLE_VIEWER, ROLE_USER;
    }

    @Id
    @Column(unique = true, nullable = false)
    private Integer employeeId;

    @NotNull
    @Column(nullable = false)
    private String firstName;

    @NotNull
    @Column(nullable = false)
    private String lastName;
    
    @Email
    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(unique = true, nullable = false)
    private String password;

    private String department;
    private String designation;
    private Role role;

    // Requirements created by this user
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Requirement> createdRequirements;

    // Requirements assigned to this user
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Requirement> assignedRequirements;
    
}