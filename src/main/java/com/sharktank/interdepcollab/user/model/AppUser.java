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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @ToString.Exclude
    private String password;

    private String department;
    private String designation;

    @ToString.Exclude
    private Role role;

    // Requirements created by this user
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Requirement> createdRequirements;

    // Requirements assigned to this user
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Requirement> assignedRequirements;
        
    // Actions done by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Action> actions;
}