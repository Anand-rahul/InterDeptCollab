package com.sharktank.interdepcollab.user.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

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
        ADMIN, VIEWER, IT_USER, BIZ_USER;
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
    @JsonIgnore
    private String password;

    private String department;
    private String designation;

    @ToString.Exclude
    private Role role;
        
    // Actions done by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Action> actions;
}